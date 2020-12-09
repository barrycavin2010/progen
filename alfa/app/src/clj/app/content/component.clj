(ns app.content.component
  (:require
    [com.stuartsierra.component :as component]
    [app.content.register :as regis]
    [app.content.engine :as engine]
    [app.utils :refer :all]))

(defrecord Content [source]
  component/Lifecycle
  (start [this])
  (stop [this]))

(defn make [content-config]
  (map->Content content-config))




