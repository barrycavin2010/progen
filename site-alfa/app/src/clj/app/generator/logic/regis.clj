(ns app.generator.logic.regis
  (:require [app.generator.logic.logic]))

(def register
  [{:folder "logic"
    :file "logic-01.html"
    :gen-fn app.generator.logic.logic/logic-01}
   {:folder "logic"
    :file "logic-02.html"
    :gen-fn app.generator.logic.logic/logic-02}])
