(ns app.content.component
  (:require
    [com.stuartsierra.component :as component]
    [app.generator.logic-one :as gen]
    [app.utils :refer :all]
    [me.raynes.fs :as fs]))

(defrecord Content []
  component/Lifecycle
  (start [this]
    (assoc this :soals (gen/generator "problem-one.html")))
  (stop [this]
    (dissoc this :soals)))

(defn make []
  (map->Content {}))


