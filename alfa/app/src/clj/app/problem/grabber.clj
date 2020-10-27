(ns alfa.problem.grabber
  (:require
    [clojure.java.io :as io]
    [alfa.utils :refer :all]
    [me.raynes.fs :as fs]
    [taoensso.timbre :as log]
    [clojure.string :as cs]
    [alfa.problem.generator :as gen]
    [taoensso.timbre :as log]
    [alfa.schema :refer :all]
    [schema.core :as s]
    [clojure.set :as cst]))

;; Generic functions

(defn problem?
  "Returns true if a directory is a problem directory."
  [dir]
  (fs/exists? (path dir "meta.edn")))

(defn grab-valid?
  "Perform files validity check for a raw problem map.
  The problem map is obtained from calling grab function."
  [problem-map]
  (let [{:keys [soal pembahasan relations answers coder bener?]}
        problem-map
        tests
        [(fn [] (boolean soal))
         (fn [] (boolean pembahasan))
         (fn [] (if (= (:type problem-map) :causal) (boolean relations) true))
         (fn [] (if (#{:blank :blank-string} (:type problem-map)) (boolean answers) true))]]
    (or (every? #(%) tests)
        (do (when (or (nil? bener?) bener?)
              (collect-error coder))
            (error coder "is too lazy to provide necessary files for the zp-problem")))))

(defn meta-valid?
  "Check whether the meta in problem fulfills the minimum requirements"
  [meta-problem]
  (let [{:keys [creator bener? title coder]} meta-problem
        res (cst/difference (set (keys meta-problem))
                            (cst/union (set (keys zp-problem-schema))
                                       #{:generator :answers :bener?
                                         :test :relations}))]
    (when (not-empty res)
      (collect-error coder)
      (error coder "thinks this is zoala, too many keys for meta" res "are superflous!"))
    (or (and (keyword? (:type meta-problem))
             (empty? res)
             (string? title)
             (string? creator)
             (string? coder)
             (not (nil? (:set? meta-problem))))
        (do (when (or (nil? bener?) bener?)
              (collect-error coder))
            (error "Invalid meta.edn, some keys are missing by" coder)))))

(defn register!
  "Simply returns the meta with uuid if it's not there yet."
  [dir]
  (let [res (cslurp (path dir "meta.edn"))]
    #_(when (:coder res)
        (let [condir (-> (parent-dir dir)
                         (path "container.edn"))
              meta (silent-try (cslurp condir))]
          (when meta
            (cspit condir (assoc meta :coder (:coder res))))))
    (-> (assoc res :id (:id res (uuid)))
        (dissoc :con-id))))

;; DB preparations grabbing functions

(defn generate
  "Generate an instance of a problem, the map received is db-type problem-map"
  [gen-fn db-problem]
  (when (and (grab-valid? db-problem)
             (meta-valid? db-problem))
    (gen/generate gen-fn db-problem)))

(defn req-grabber
  "Grabbing the content of problem files."
  [bener? coder dir]
  (-> (merge {} (when-let [con (silent-try (slurp (path dir "generator.edn")))]
                  {:generator con}))
      (merge (when-let [con (silent-try (slurp (path dir "test.edn")))]
               {:test con}))
      (merge (when-let [con (if (or bener? (nil? bener?))
                              (no-throw (collect-error coder)
                                        (str coder " forget to put soal.html in the zp-problem!" (conpath dir))
                                        (slurp (path dir "soal.html")))
                              (silent-try (slurp (path dir "soal.html"))))]
               {:soal con}))
      (merge (when-let [con (if (or bener? (nil? bener?))
                              (no-throw (collect-error coder)
                                        (str coder " forget to put pembahasan.html in the zp-problem!" (conpath dir))
                                        (slurp (path dir "pembahasan.html")))
                              (silent-try (slurp (path dir "pembahasan.html"))))]
               {:pembahasan con}))
      (merge (when-let [con (silent-try (slurp (path dir "relations.edn")))]
               {:relations con}))
      (merge (when-let [con (silent-try (slurp (path dir "answers.edn")))]
               {:answers con}))
      (merge (when-let [script (silent-try (slurp (path dir "script.js")))]
               {:script script}))))

(defn process!
  "Grab a problem directory and create a raw problem map.
  The problem is not yet validity-checked"
  [meta-con gen-fn dir]
  (when (problem? dir)
    (when-let [meta (try (cslurp (path dir "meta.edn"))
                         (catch Exception e
                           (error "Unreadable meta.edn at"
                                  (conpath dir)
                                  "by" (collect-error (:coder meta-con "Someone")))
                           (error "Error while reading meta.edn caused by" (.getMessage e))))]
      (if (map? meta)
        (let [{:keys [bener? coder]} meta
              others (req-grabber bener? coder dir)
              db-map (merge meta others)]
          (when (try (generate gen-fn db-map)
                     (catch Exception e
                       (when (or (nil? (:bener? meta)) (:bener? meta))
                         (collect-error coder))
                       (error (str coder "'s") "problem error at" (conpath dir))
                       (error "Problem error caused by" (.getMessage e))))
            (merge db-map (register! dir) {:dir (str dir)})))
        (error (collect-error (:coder meta-con "Someone"))
               "meta.edn must be a map" (conpath dir))))))

(defn register-zp-problems!
  "Registering zp-problems by listing the dir of zp_problem and generate all problems there."
  [meta-con gen-fn refs con-id dir]
  (let [resi (->> (fs/list-dir dir)
                  (filter problem?)
                  (keep #(process! meta-con gen-fn %))
                  (map-indexed #(assoc %2 :sequence %))
                  vec)
        fext (fn [x] (if (some #{(:id x)} @(:zp-problem-id refs)) true false))
        resiko (filterv fext resi)
        res (filterv #(not (fext %)) resi) ]
    (when (not= (map :id resi)
                (distinct (map :id resi)))
      (error (collect-error (:coder meta-con "Someone")) "matanya mulai sutur"
             (conpath dir)))
    (when (not-empty resiko)
      (loop [[r & rs] resiko]
        (when r
          (error "Duplicated zp-problem uuid is detected in" (conpath dir)
                 "with" (conpath (:dir (@(:zp-problem refs) (:id r))))
                 "this error presented by" (collect-error (:coder meta-con "Someone")))
          (recur rs))))
    (dosync (alter (:container refs)
                   #(->> (mapv (fn [x] (-> (select-keys x [:id :title :sequence])
                                           (assoc :con-id con-id))) res)
                         (assoc-in % [con-id :con-zp-problems])))
            (alter (:zp-problem refs)
                   #(->> (map (fn [x] (dissoc x :sequence)) res)
                         (zipmap (map :id res))
                         (merge %)))
            (alter (:zp-problem-id refs) #(concat % (mapv :id res))))))


