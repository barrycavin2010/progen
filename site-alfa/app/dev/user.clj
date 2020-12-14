(ns user
  (:require
    [app.viewersys.system :as viewersys]
    [app.sitesys.system :as sitesys]
    [com.stuartsierra.component :as component]
    [app.utils :refer :all]))

(defonce dev-system (atom nil))

(defn start
  "Starting the viewer"
  [which-system]
  (->> (condp = which-system
         :viewersys (viewersys/create-system which-system)
         :sitesys (sitesys/create-system which-system))
       (component/start-system)
       (reset! dev-system)))

(defn stop []
  (swap! dev-system component/stop-system))

(defn restart
  []
  (let [which-system (:which-system @dev-system)]
    (do (stop)
        (print "Restarting the system in 2 seconds... ")
        (Thread/sleep 100)
        (println "plus/minus 5 minutes.")
        (Thread/sleep 100)
        (start which-system))))

