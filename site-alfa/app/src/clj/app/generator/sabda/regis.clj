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
              {:problem-name "arit-02"
               :file   "arit-02.html"
               :gen-fn app.generator.sabda.arit/arit-02}
              {:problem-name "arit-03"
               :file   "arit-03.html"
               :gen-fn app.generator.sabda.arit/arit-03}
              {:problem-name "arit-03"
               :file   "arit-04.html"
               :gen-fn app.generator.sabda.arit/arit-04}
              {:problem-name "grammar-01"
               :file   "grammar-01.html"
               :gen-fn app.generator.sabda.grammar/grammar-01}]})
