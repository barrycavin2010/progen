(ns user
  (:require [app.appsys.system :as system]
            [com.stuartsierra.component :as component]
            [app.utils :refer :all]))

(defonce dev-system (atom nil))

(defn start
  "Starting the viewer"
  ([] (->> (system/create-system)
           (component/start-system)
           (reset! dev-system))))

(defn stop []
  (swap! dev-system component/stop-system))

(defn restart
  []
  (do (stop)
      (print "Restarting the system in 2 seconds... ")
      (Thread/sleep 100)
      (println "plus/minus 5 minutes.")
      (Thread/sleep 100)
      (start)))

