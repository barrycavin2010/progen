(ns app.content.component
  (:require
    [com.stuartsierra.component :as component]
    [app.content.register :as regis]
    [app.content.grabber :as grabber]
    [app.utils :refer :all]))

(declare grab-produce)

(defrecord Content [source]
  component/Lifecycle
  (start [this]
    (let [all-makers (regis/soal-map)
          problems (grab-produce source all-makers)
          problem-vec (vec (vals problems))]
      (pres problem-vec)
      (assoc this :problems problem-vec
                  :problem-map problems)))
  (stop [this]
    (dissoc this :problems)))

(defn make [content-config]
  (map->Content content-config))

(defn grab-produce
  "Grabbing problems based on source & registered problems"
  [source all-makers]
  (let [reference (->> (grabber/grab source all-makers)
                       (map :problems)
                       (apply concat)
                       vec)
        processed (mapv grabber/produce reference)]
    (-> (map #(get-in % [:meta :template-id]) processed)
        (zipmap processed))))




