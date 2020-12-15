(ns app.producer.component
  (:require
    [com.stuartsierra.component :as component]
    [app.producer.register :as regis]
    [app.producer.grabber :as grabber]
    [app.utils :refer :all]))

(declare grab-produce)

(defrecord Producer [source]
  component/Lifecycle
  (start [this]
    (let [all-data (regis/soal-map)
          problems (grab-produce source all-data)]
      (assoc this :problems problems)))
  (stop [this]
    (dissoc this :problems)))

(defn make [content-config]
  (map->Producer content-config))

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




