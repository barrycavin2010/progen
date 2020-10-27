(ns alfa.webapp.content
  (:require
    [alfa.utils :refer :all]
    [taoensso.timbre :as log]
    [com.stuartsierra.component :as component]
    [alfa.problem.grabber :as pro-grab]
    [alfa.problem.generator :as pro-gen]
    [alfa.grabber.container :as con]))

(defrecord Content [grabber]
  component/Lifecycle
  (start [this]
    (let [res {:data        (:content-state grabber)
               :resources   (:target-dirs grabber)
               :generate-fn (get-in grabber [:library :generate-fn])}]
      (-> (merge this res)
          (assoc :top-cgs (->> (vals @(:content-group (:data res)))
                               (filterv #(== 1 (:level %)))
                               (sort-by :sequence)
                               vec)))))
  (stop [this]
    this))

(defn make
  "Constructor function for Content"
  []
  (map->Content {}))

(defn get-cgs
  "Returns the cg by id, or all cgs by parent id"
  ([content cg-id]
   (if-let [res (@(get-in content [:data :content-group]) cg-id)]
     res
     (pro-rep cg-id " doesnt exist")))
  ([content parent-id one-level-only?]
   (->> (get-cgs content parent-id)
        :cg-children
        (mapv :id)
        (select-keys @(get-in content [:data :content-group]))
        vals
        (sort-by :sequence)
        vec)))

(defn top-cgs
  [content]
  (->> (vals @(get-in content [:data :content-group]))
       (filterv #(== 1 (:level %)))
       (sort-by :sequence)
       vec))

(comment
  (defn cg-by-level
    "Returns all cgs in that level"
    [content level]
    (->> (get-in content [:data :content-group])
         vals
         (filterv #(= level (:level %)))))

  (defn get-znet-problem
    "Returns zproblem by its id or by con-id"
    ([content id]
     (get-in content [:data :znet-problem id]))
    ([content con-id as-vector?]
     (->> (get-in content [:data :container con-id])
          :con-znet-problems)))

  (defn get-video
    "Returns video by its id or by con-id"
    ([content video-id]
     (get-in content [:data :video video-id]))
    ([content con-id as-vector?]
     (->> (get-in content [:data :container con-id])
          :con-videos)))

  (defn get-notes
    "Returns notes by its id or by con-id"
    ([content notes-id]
     (get-in content [:data :notes notes-id]))
    ([content con-id as-vector?]
     (->> (get-in content [:data :container con-id])
          :con-notes
          (map :id)
          (mapv #(get-notes content %)))))

  (defn get-zp-problem
    "Returns zp-problem by its id or by con-id"
    ([content id]
     (->> [:data :zp-problem id]
          (get-in content)
          pro-gen/generate))
    ([content con-id as-vector?]
     (->> (get-in content [:data :container con-id])
          :con-zp-problems
          (map :id)
          (mapv #(get-zp-problem content %))
          (mapv #(pro-gen/generate %))))))

(defn get-old-problem
  [content oldpro-id]
  (if-let [data (@(get-in content [:data :old-problem]) oldpro-id)]
    (-> (rand-nth data)
        :problem read-string
        (assoc :id oldpro-id))
    (do (error "Can't find old problem with id" oldpro-id)
        (throw (Exception. "Old problem doesnt exist")))))

(defn pl-helper
  [meta content pl-elmt]
  (if (= :error (:id pl-elmt))
    {:id   :error
     :type (:type pl-elmt)}
    (if-let [res (no-throw (str (collect-error (:coder meta "Someone"))
                                " made playlist type error : ") (:type pl-elmt)
                           (if (= :old-problem (:type pl-elmt))
                             (get-old-problem content (:id pl-elmt))
                             (@(get-in content [:data (:type pl-elmt)]) (:id pl-elmt))))]
      (if (= :zp-problem (:type pl-elmt))
        (if-let [resi (try (-> (:generate-fn content)
                               (pro-gen/generate res)
                               (assoc :id (:id pl-elmt)))
                           (catch Exception e
                             (error "While generating problem with id" (:id pl-elmt))
                             (error (collect-error (:coder meta "Someone"))
                                    "regeneration error caused by" (.getMessage e))))]
          resi
          (do (when (= :zp-problem (:type pl-elmt))
                (when (some #{(:id pl-elmt)} @(get-in content [:data :zp-problem-id]))
                  (error "It's weird though, this problem should already be added... which means successfully generated at the first attempt... hmmmmm")))
              {:id :error :type :zp-problem}))
        res)
      (do (when (not= :old-problem (:type pl-elmt))
            (error "Cant find" (:type pl-elmt) "of id" (:id pl-elmt) "in content")
            (error "This error presented by" (collect-error (:coder meta "Someone"))))
          {:id   (if (= :old-problem (:type pl-elmt)) (:id pl-elmt) :error)
           :type (:type pl-elmt)}))))

(defn get-playlist
  "Returns complete data of playlist by its con-id"
  [meta content con-pl]
  (-> #(assoc % :data (pl-helper meta content %))
      (mapv con-pl)))

(defn get-container
  "Returns container by id or containers by cg-id, if it's by con-id then will grab the
  detail of the container including playlist element, otherwise returns a simple list of
  ids and title of the containers under a certain cg-id."
  ([content con-id]
   (when-let [container (@(get-in content [:data :container]) con-id)]
     (let [meta container
           pl (get-playlist meta content (:playlist container))
           res (filter #(and (= :error (get-in % [:data :id]))
                             (not= :video (get-in % [:data :type]))) pl)]
       (when (not-empty res)
         (do (error (str "Some of this "
                         (conpath (:temp-dir container))
                         " playlist element are error"))
             (error "The error are for types "
                    (->> (map #(get-in % [:data :type]) res)
                         (interpose "  ")
                         (apply str)))
             (error "This error presented by : "
                    (collect-error (:coder meta "Someone"))
                    "!!!!!!!!")))
       (assoc-in container [:playlist] (vec (sort-by :sequence pl))))))
  ([content cg-id as-vector?]
   (->> (get-cgs content cg-id)
        :cg-containers
        (sort-by :sequence)
        vec)))












