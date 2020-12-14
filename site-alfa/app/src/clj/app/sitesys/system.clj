(ns app.sitesys.system
  (:require
    [com.stuartsierra.component :as component]
    [app.sitesys.config :refer [config]]
    [clojure.tools.namespace.repl :refer [refresh]]
    [app.logic.dbase :as db]
    [app.utils :refer :all]
    [app.sitesys.server :as immut]
    [app.sitesys.handler :as http]))

(defn create-system
  "It creates a system, and return the system, but not started yet"
  []
  (let [{:keys [server dbase]}
        (config)]
    (component/system-map
      :dbase (db/make dbase)
      :handler (component/using (http/make) [:dbase])
      :server (component/using (immut/make server) [:handler]))))

(defonce system (atom nil))

(defn init
  "Function to initiate the system"
  []
  (reset! system (create-system)))

(defn start
  "Function to start the system, that is starting all the components and resolving
  the dependencies of each component."
  []
  (swap! system component/start-system))

(defn stop
  "Function to stop the system, and stop all of its components according to the
  dependencies of each component."
  []
  (swap! system component/stop-system))

(defn go
  "The function to be called from the REPL for starting the system"
  []
  (init)
  (start)
  @system)
