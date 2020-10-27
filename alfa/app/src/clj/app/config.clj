(ns alfa.config
  (:require [alfa.utils :refer :all]
            [taoensso.timbre :as log]))

(defn config
  "Reading the config, either intra-project or extra-project"
  [mode]
  ((cslurp "resources/config.edn") mode))
