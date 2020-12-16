(ns app.applogic.dbase
  (:require
    [com.stuartsierra.component :as component]
    [taoensso.carmine :as car :refer [wcar]]
    [app.utils :refer :all]))

(declare grab-content init-db set-cronjob load-db)

(defrecord Dbase [source redis]
  component/Lifecycle
  (start [this]
    (println "Dbase component started")
    (let [db (init-db)]))
  (stop [this] this))

(defn make [db-config]
  (map->Dbase db-config))

(defn load-db
  "Load content data from existing db"
  [{:keys [conn] :as dbase}]
  {:template-ids (wcar conn (car/get :template-ids))
   :templates    (wcar conn (car/get :templates))
   :problem-map (wcar conn (car/get :problem-map))
   :problems (wcar conn (car/get :problems))})

(defn init-db
  []
  {:content       {:template-ids    []
                   :templates       {:math    []
                                     :logic   []
                                     :english []}
                   :problem-ids-map {}
                   :problems        {}}
   :content-stats (ref {:templates {:math    []
                                    :logic   []
                                    :english []}
                        :problems  {}})})

