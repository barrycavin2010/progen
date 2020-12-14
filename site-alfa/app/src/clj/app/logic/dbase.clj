(ns app.logic.dbase
  (:require
    [com.stuartsierra.component :as component]
    [app.utils :refer :all]))

(defrecord Dbase [source redis]
  component/Lifecycle
  (start [this]
    (println "Dbase component started")
    (assoc this :templates {:pesan "Amitabacan"}))
  (stop [this] this))

(defn make [db-config]
  (map->Dbase db-config))







