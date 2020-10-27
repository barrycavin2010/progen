(ns app.config
  (:require
    [app.utils :refer :all]))

(defn config
  "Reading the config, either intra-project or extra-project"
  [mode]
  ((cslurp "resources/config.edn") mode))
