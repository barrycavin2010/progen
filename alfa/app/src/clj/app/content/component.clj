(ns app.content.component
  (:require
    [com.stuartsierra.component :as component]
    [app.utils :refer :all]))

(defrecord Content [source max-view soal-choice]
  component/Lifecycle
  (start [this])
  (stop [this]))

(defn make [content-config]
  (map->Content content-config))




