(ns app.handler
  (:require
    [compojure.core :refer [GET POST context routes]]
    [compojure.route :refer [resources files not-found]]
    [com.stuartsierra.component :as component]
    [ring.util.response :as resp]
    [app.utils :refer :all]
    [app.webapp.routes :refer :all]
    [me.raynes.fs :as fs]))

(defrecord Handler [content]
  component/Lifecycle
  (start [this]
    (assoc this :routes (main-routes content)))
  (stop [this]
    this))

(defn make []
  (map->Handler {}))
