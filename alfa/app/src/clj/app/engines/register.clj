(ns app.engines.register
  (:require [app.generator.logic :as logic]))

(defn soal-map
  "VERY IMPORTANT: basically a registration point when adding a generator"
  []
  {"logic-1" {:gen-fn logic/one
              :dir    "logic-1"}})
