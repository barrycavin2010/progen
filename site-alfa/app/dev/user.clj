(ns user
  (:require
    [app.viewersys.system :as viewersys]
    [app.sitesys.system :as sitesys]
    [com.stuartsierra.component :as component]
    [taoensso.carmine :as car :refer [wcar]]
    [clojure.set :as cset]
    [app.utils :refer :all]))

(defonce dev-system (atom nil))

(def setmin cset/difference)
(def setjoin cset/union)
(def setinter cset/intersection)

;;===== SYSTEM RELATED FUNCTIONS ======

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

;; ======= INITIAL DATABASE INSERTION ======

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

(defn- init-store
  []
  (let [{:keys [template-ids templates problems problem-map]} (load-files)]
    (time
      (wcar conn
            (car/set :template-ids template-ids)
            (car/set :templates templates)
            (car/set :problems problems)
            (car/set :problem-map problem-map)))))

;;===== ADDING MORE SOALS INTO DB======

(defn sync-db
  []
  (let [conf (config)
        file-templates (cslurp (str (get-in conf [:dbase :content]) "problems.edn"))
        [db-template-ids
         db-templates
         db-problems
         db-problem-map] (wcar conn
                               (car/get :template-ids)
                               (car/get :templates)
                               (car/get :problems)
                               (car/get :problem-map))
        filtered-templates (remove #((set db-template-ids) (:template-id %)) file-templates)
        problems-by-template-id (-> #(do [(:template-id %) (load-problem (:edn-file %))])
                                    (mapv filtered-templates))
        problem-list (->> (mapv second problems-by-template-id)
                          (apply concat)
                          vec)
        problems (zipmap (mapv :problem-id problem-list) problem-list)
        problem-map (zipmap (mapv first problems-by-template-id)
                            (mapv #(mapv :problem-id (second %)) problems-by-template-id))]
    (pres db-template-ids)
    (pres filtered-templates)
    (pres problem-map)
    (pres problems)
    (wcar conn
          (car/set
            :template-ids
            (vec (setjoin
                   (set db-template-ids)
                   (set (map :template-id file-templates)))))
          (car/set
            :templates
            (merge-with #(vec (setjoin (set %1) (set %2)))
                        db-templates
                        (group-by :topic filtered-templates)))
          (car/set :problems (merge db-problems problems))
          (car/set :problem-map (merge db-problem-map problem-map)))))

































