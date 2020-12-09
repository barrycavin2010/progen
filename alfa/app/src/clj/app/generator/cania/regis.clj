(ns app.generator.cania.regis
  (:require [app.generator.cania.logic :as logic]))

(defn register
  "The name of the keyword must reflects the name of the html file"
  []
  {:folder   "cania"
   :problems [{:problem-name "logic-01"
               :file   "logic-01.html"
               :gen-fn app.generator.cania.logic/logic-01}
              {:problem-name "logic-02"
               :file   "logic-02.html"
               :gen-fn app.generator.cania.logic/logic-01}]})
