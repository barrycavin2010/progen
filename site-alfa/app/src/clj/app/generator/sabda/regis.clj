(ns app.generator.sabda.regis
  (:require [app.generator.sabda.arit]
            [app.generator.sabda.grammar]
            [app.generator.sabda.logic]))

(defn register
  "The name of the keyword must reflects the name of the html file"
  []
  {:folder   "sabda"
   :problems [{:problem-name "logic-01"
               :file   "logic-01.html"
               :gen-fn app.generator.sabda.logic/logic-01}
              {:problem-name "arit-01"
               :file   "arit-01.html"
               :gen-fn app.generator.sabda.arit/arit-01}
              {:problem-name "grammar-01"
               :file   "grammar-01.html"
               :gen-fn app.generator.sabda.grammar/grammar-01}]})
