(ns user
  (:require [alfa.system :as system]
            [com.stuartsierra.component :as component]
            [alfa.utils :refer :all]
            [alfa.grabber.component :as grabber]))

(defonce dev-system (atom nil))

(defn start
  "Starting the viewer"
  ([mode]
   (->> (system/create-system mode)
        (component/start-system)
        (reset! dev-system)))
  ([] (start :viewer)))

(defn stop []
  (swap! dev-system component/stop-system))

(defn copy-files []
  (grabber/spit-contents! (get-in @dev-system [:grabber])))

(defn restart
  []
  (let [mode (get-in @dev-system [:mode])]
    (stop)
    (print "Restarting the system in 2 seconds... ")
    (Thread/sleep 2000)
    (println "plus/minus 5 minutes.")
    (Thread/sleep 1000)
    (start mode)))

;content-dev
(defn mathmode "activate for repl/emac-cider eval math problems"
  []
  (require '[alfa.libs.latex :as latex]))
