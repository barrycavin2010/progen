(ns user
  (:require [app.system :as system]
            [com.stuartsierra.component :as component]
            [app.utils :refer :all]))

(defonce dev-system (atom nil))

(defn start
  "Starting the viewer"
  ([mode]
   (->> (system/create-system mode)
        (component/start-system)
        (reset! dev-system)))
  ([] (start :dev)))

(defn stop []
  (swap! dev-system component/stop-system))

(defn restart
  []
  (let [mode (get-in @dev-system [:mode])]
    (stop)
    (print "Restarting the system in 2 seconds... ")
    (Thread/sleep 500)
    (println "plus/minus 5 minutes.")
    (Thread/sleep 500)
    (start mode)))

