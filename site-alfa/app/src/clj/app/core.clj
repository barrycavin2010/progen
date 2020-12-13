(ns app.core
  (:gen-class)
  (:require [app.appsys.system :as system]
            [com.stuartsierra.component :as component]))

(defn -main [& x]
  (component/start-system (system/create-system)))
