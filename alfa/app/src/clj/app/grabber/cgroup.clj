(ns alfa.grabber.cgroup
  (:require
    [clojure.string :as cs]
    [me.raynes.fs :as fs]
    [clojure.java.io :as io]
    [alfa.utils :refer :all]
    [alfa.grabber.container :as container]
    [taoensso.timbre :as log]))

(defn cg?
  "Returns true if a directory is a cg."
  [dir]
  (and (fs/directory? dir)
       (let [children (fs/list-dir dir)
             valchild (filter #(= "cg.edn" (fs/base-name %)) children)]
         (and (boolean (not-empty valchild))
              (map? (try (cslurp (first valchild))
                         (catch Exception e
                           (error "Unreadable cg.edn in" (conpath dir))
                           (error "cg.edn error caused by" (.getMessage e)))))))))

(defn resources?
  "Returns true if dir is a resources directory"
  [dir]
  (and (fs/directory? dir)
       (= "resources" (fs/base-name dir))))

(defn copy-resources!
  "Copy the resources from dir into target dir in the component"
  [refs target-dirs dir]
  (let [res-ref (:resources refs)
        res-dir (:resources target-dirs)
        res-files (->> (fs/list-dir dir)
                       (filter #(#{".js" ".css"} (fs/extension %))))]
    (loop [[rf & rfs] res-files]
      (when rf
        (fs/copy rf (path res-dir (fs/base-name rf)))
        (dosync (alter res-ref #(conj % (fs/base-name rf))))
        (recur rfs)))))

(defn register!
  "Register a cg by collecting it's meta and add uuid if it doesn't yet exist."
  [dir parents level sequ]
  (let [base-name (fs/base-name dir)
        meta-file (path dir "cg.edn")
        title (apply str (drop 3 base-name))
        meta (try (cslurp meta-file)
                  (catch Exception e
                    (error "cg.edn error caused by" (.getMessage e))))]
    (merge meta
           {:id            (:id meta (uuid))
            :title         title
            :cg-parents    parents
            :url-title     (cs/replace title #" " "_")
            :sequence      sequ
            :level         level
            :cg-containers []})))

;; TODO Refactor this ugly function!!!

(defn grab-and-register!
  "Recursively grab a cg directory and register the meta data"
  ([gen-fn refs target-dirs dir parents level sequ]
   (let [cg-map (when (cg? dir)
                  (register! dir parents level sequ))
         children (->> (fs/list-dir dir)
                       (filter fs/directory?)
                       (sort-by fs/base-name))
         cg-childrens (filter cg? children)
         containers (filter container/container? children)
         resources (filter resources? children)]
     (loop [[rsd & rsds] resources]
       (when rsd
         (copy-resources! refs target-dirs rsd)
         (recur rsds)))
     (when cg-map
       (when (seq containers)
         (loop [[idxl & idxs] (map-indexed #(do [%1 %2]) containers)]
           (when idxl
             (let [[idx con] idxl]
               (container/grab-and-register! gen-fn refs target-dirs con (:id cg-map) idx))
             (recur idxs))))
       (let [cg-containers (->> (vals @(:container refs))
                                (filter #(= (:id cg-map) (:cg-id %)))
                                (mapv #(select-keys % [:id :title :sequence])))]
         (if (empty? cg-childrens)
           (let [new-map (-> (merge cg-map {:cg-children []})
                             (assoc :cg-containers cg-containers))]
             (dosync (alter (:content-group refs) #(assoc % (:id cg-map) new-map)))
             [new-map])
           (let [parent-map (when cg-map
                              {:id       (:id cg-map)
                               :distance 1
                               :title    (:title cg-map)})
                 new-parents (when parent-map
                               (-> #(update-in % [:distance] inc)
                                   (mapv parents)
                                   (conj parent-map)))
                 cg-children (->> cg-childrens
                                  (map-indexed #(grab-and-register! gen-fn refs target-dirs %2 new-parents (inc level) %1))
                                  (keep identity)
                                  (apply concat)
                                  vec)
                 new-map (-> (merge cg-map
                                    {:cg-children
                                     (->> (filter #(== (inc level) (:level %)) cg-children)
                                          (mapv #(select-keys % [:id :title :sequence])))})
                             (assoc :cg-containers cg-containers))]
             (dosync (alter (:content-group refs) #(assoc % (:id cg-map) new-map)))
             (if (empty? cg-children)
               [new-map]
               (conj cg-children new-map))))))))
  ([gen-fn refs path target-dirs]
   (do (->> (fs/list-dir path)
            (filter resources?)
            (mapv #(copy-resources! refs target-dirs %)))
       (->> (fs/list-dir path)
            (filter cg?)
            (sort-by fs/base-name)
            (map-indexed #(grab-and-register! gen-fn refs target-dirs %2 [] 1 %1))
            (apply concat)
            vec))))



