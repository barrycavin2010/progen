(ns app.generator.sabda.regis)

(defn register
  "The name of the keyword must reflects the name of the html file"
  []
  {:folder   "sabda"
   :problems {"logic-01" {:file   "logic-01.html"
                          :gen-fn app.generator.sabda.logic/logic-01}}})
