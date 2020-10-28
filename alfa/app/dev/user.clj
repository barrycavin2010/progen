(ns user
  (:require [app.system :as system]
            [com.stuartsierra.component :as component]
            [app.utils :refer :all]))

(defonce dev-system (atom nil))

(defn start
  "Starting the viewer"
  ([soal-choice]
   (->> (system/create-system soal-choice)
        (component/start-system)
        (reset! dev-system)))
  ([] (start nil)))

(defn stop []
  (swap! dev-system component/stop-system))

(defn restart
  []
  (let [soal-choice (get-in @dev-system [:soal-choice])]
    (stop)
    (print "Restarting the system in 2 seconds... ")
    (Thread/sleep 100)
    (println "plus/minus 5 minutes.")
    (Thread/sleep 100)
    (start soal-choice)))

