(ns app.logic.dbase
  (:require
    [com.stuartsierra.component :as component]
    [app.utils :refer :all]))

(defrecord Dbase [source]
  component/Lifecycle
  (start [this] this)
  (stop [this] this))

(defn make [db-config]
  (map->Dbase db-config))







