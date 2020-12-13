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
    (let [all-makers (regis/soal-map)
          problems (grab-produce source all-makers)
          templates (mapv :meta (vals problems))]
      (assoc this :templates templates
                  :problem-map problems)))
  (stop [this]
    (dissoc this :problems :problem-map)))

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




