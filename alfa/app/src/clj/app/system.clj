(ns alfa.system
  (:require
    [com.stuartsierra.component :as component]
    [alfa.config :refer [config]]
    [clojure.tools.namespace.repl :refer [refresh]]
    [alfa.utils :refer :all]
    [alfa.grabber.component :as grabber]
    [alfa.server :as immut]
    [alfa.webapp.content :as content]
    [alfa.libs :as libs]
    [alfa.handler :as http]))

(defn create-system
  "It creates a system, and return the system, but not started yet"
  [mode]
  (let [{:keys [lib-source content-source content-target server video-target]}
        (config mode)]
    (component/system-map
      :grabber (-> (grabber/make mode video-target)
                   (component/using [:content-source :content-target :library]))
      :library (libs/make lib-source)
      :handler (component/using (http/make) [:content])
      :content (component/using (content/make) [:grabber])
      :server (component/using (immut/make server) [:handler])
      :content-source content-source
      :content-target content-target
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

(comment
  (defn reset
    "Reset the system in REPL after changing some codes"
    []
    (stop)
    (info "Wait 20 secs")
    (Thread/sleep 15000)
    (refresh :after 'alfa.system/go)))
