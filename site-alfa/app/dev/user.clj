(ns user
  (:require
    [app.viewersys.system :as viewersys]
    [app.sitesys.system :as sitesys]
    [com.stuartsierra.component :as component]
    [taoensso.carmine :as car :refer [wcar]]
    [app.utils :refer :all]))

(defonce dev-system (atom nil))

(defn start
  "Starting the viewer"
  [which-system]
  (->> (condp = which-system
         :viewer (viewersys/create-system which-system)
         :site (sitesys/create-system which-system))
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

;; DATABASE stuffs

(defonce conn {:pool {} :spec {}})

(defn config
  []
  (cslurp "resources/config-site.edn"))

(defn- load-problem
  [filename]
  (let [path (str (get-in (config) [:dbase :content])
                  "problems/"
                  filename)]
    (cslurp path)))

(defn- load-files
  []
  (let [conf (config)
        templates (cslurp (str (get-in conf [:dbase :content]) "problems.edn"))
        problems-by-template-id (-> #(do [(:template-id %) (load-problem (:edn-file %))])
                                    (mapv templates))
        problem-list (->> (mapv second problems-by-template-id)
                          (apply concat)
                          vec)
        problems (zipmap (mapv :problem-id problem-list) problem-list)
        problem-map (zipmap (mapv first problems-by-template-id)
                            (mapv #(mapv :problem-id (second %)) problems-by-template-id))]
    {:template-ids (mapv :template-id templates)
     :templates    (group-by :topic templates)
     :problems     problems
     :problem-map  problem-map}))

(defn init-store
  []
  (let [{:keys [template-ids templates problems problem-map]} (load-files)]
    (time
      (wcar conn
            (car/set :template-ids template-ids)
            (car/set :templates templates)
            (car/set :problems problems)
            (car/set :problem-map problem-map)))))

































