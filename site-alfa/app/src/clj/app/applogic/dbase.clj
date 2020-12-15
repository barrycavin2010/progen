(ns app.applogic.dbase
  (:require
    [com.stuartsierra.component :as component]
    [app.utils :refer :all]))

(declare grab-content init-db set-cronjob load-db)

(defrecord Dbase [source redis]
  component/Lifecycle
  (start [this]
    (println "Dbase component started")
    (let [db (ref nil)]
      (init-db source db)
      (load-db db redis)
      (set-cronjob db redis)
      (merge this
             {:templates {:pesan "Amitabacan"}
              :conn redis
              :db db})))
  (stop [this] this))

(defn make [db-config]
  (map->Dbase db-config))

(defn init-db
  [source db]
  (dosync (ref-set db {:content {:template-math {}
                                 :template-english {}
                                 :template-logic {}
                                 :data-math {}
                                 :data-english {}
                                 :data-logic}
                       :user {:profile {}
                              :stat-math {}
                              :stat-logic {}
                              :stat-english {}
                              :stat-template-math {}
                              :stat-template-logic {}
                              :stat-template-english {}}
                       :stat {:template-math {}
                              :template-english {}
                              :template-logic {}
                              :data-math {}
                              :data-english {}
                              :data-logic}})))









