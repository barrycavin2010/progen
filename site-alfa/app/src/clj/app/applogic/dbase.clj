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
    (let [ref-sorted-templates (ref nil)]
      (merge this
             {:conn redis})))
  (stop [this] this))

(defn make [db-config]
  (map->Dbase db-config))

(defn set-cronjob
  [conn db]
  "First part is to re-sort the levels and store the new level into db"
  (let [{:keys [math logic english campur]} (-> @(:content-stats db)
                                                (get :sorted-templates))])
  )

(defn load-db
  "Load content data from existing db"
  [conn]
  {:template-ids (wcar conn (car/get :template-ids))
   :templates    (wcar conn (car/get :templates))
   :problem-map  (wcar conn (car/get :problem-map))
   :problems     (wcar conn (car/get :problems))})






