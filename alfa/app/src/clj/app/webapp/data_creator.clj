(ns alfa.webapp.data-creator
  (:require
    [alfa.utils :refer :all]
    [taoensso.timbre :as log]
    [com.stuartsierra.component :as component]
    [alfa.webapp.content :as webcon]
    [alfa.grabber.component :as grab-comp]
    [alfa.problem.generator :as gen]))

(defn cg-details
  "Returns the information regarding a cg, with its children
  both the children cgs or the containers"
  [content cg-id]
  (if (= "top" cg-id)
    {:active-cg   {:id    "top"
                   :title "Kategori pelajaran"}
     :cg-children (->> (webcon/top-cgs content)
                       (sort-by :sequence))
     :containers  []}
    (let [res (webcon/get-cgs content cg-id)
          cg-children (webcon/get-cgs content cg-id true)
          containers (webcon/get-container content cg-id true)
          result {:active-cg   {:id    cg-id
                                :title (:title res)}
                  :cg-children (->> (mapv #(assoc % :type :content-group) cg-children)
                                    (sort-by :sequence))
                  :containers  (->> (mapv #(assoc % :type :container) containers)
                                    (sort-by :sequence))}]
      result)))

(defn cg-details-by-child
  "Returns the cg details of the parent cg"
  [content cg-id]
  (if-let [res (->> (webcon/get-cgs content cg-id)
                    :cg-parents
                    (sort-by :distance)
                    first :id)]
    (cg-details content res)
    (cg-details content "top")))

(defn container-details
  "Get the details for the container, including elements in playlist"
  [content con-id]
  (webcon/get-container content con-id))

(defn update-content!
  "Update all content"
  [content]
  (grab-comp/update-content! (:grabber content)))

(defn generate-old-problem
  "Regenerate old problem aka random a different one"
  [content id]
  (if-let [data (silent-try (webcon/get-old-problem content id))]
    {:id id :type :old-problem :data data}
    {:id id :type :old-problem :data {:id :error :type :old-problem}}))

(defn generate-zp-problem
  "Generate another instance of zp-problem"
  [content zpro-id]
  (if-let [result (try (->> (@(get-in content [:data :zp-problem]) zpro-id)
                            (gen/generate (:generate-fn content)))
                       (catch Exception e
                         (error "Error generated an instance of zp-problem, that actually should be okay!?")))]
    {:id zpro-id :type :zp-problem :data (assoc result :id zpro-id)}
    {:id zpro-id :type :zp-problem :data {:id :error :type :zp-problem}}))

