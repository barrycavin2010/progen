(ns app.system
  (:require
    [com.stuartsierra.component :as component]
    [app.config :refer [config]]
    [clojure.tools.namespace.repl :refer [refresh]]
    [app.content.component :as cont]
    [app.utils :refer :all]
    [app.server :as immut]
    [app.handler :as http]))

(defn create-system
  "It creates a system, and return the system, but not started yet"
  [soal-choice]
  (let [{:keys [server content]}
        (config)]
    (component/system-map
      :content (cont/make (assoc content :soal-choice soal-choice))
      :handler (component/using (http/make) [:content])
      :server (component/using (immut/make server) [:handler])
      :soal-choice soal-choice)))

(defonce system (atom nil))

(defn init
  "Function to initiate the system"
  ([] (init nil))
  ([soal-choice]
   (reset! system (create-system soal-choice))))

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
