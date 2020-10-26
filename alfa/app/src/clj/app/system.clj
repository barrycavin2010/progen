(ns app.system
  (:require
    [com.stuartsierra.component :as component]
    [app.config :refer [config]]
    [clojure.tools.namespace.repl :refer [refresh]]
    [app.utils :refer :all]))

(defn create-system
  "It creates a system, and return the system, but not started yet"
  [mode]
  (let [{:keys []}
        (config mode)]
    (component/system-map
      :mode mode)))

(defonce system (atom nil))

#_(defn system-map
    "A Function to print-out the system map to the repl.
    Basically a development tool only"
    ([] (system-map []))
    ([path-to-key]
     (clojure.pprint/pprint (get-in system path-to-key))))

(defn init
  "Function to initiate the system"
  ([] (init :dev))
  ([mode]
   (reset! system (create-system mode))))

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


