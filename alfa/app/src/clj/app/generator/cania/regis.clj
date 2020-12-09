(ns app.generator.cania.regis)

(defn register
  "The name of the keyword must reflects the name of the html file"
  []
  {:folder   "cania"
   :problems {"logic-01" {:file   "logic-01.html"
                          :gen-fn app.generator.cania.logic/logic-01}
              "logic-02" {:file   "logic-02.html"
                          :gen-fn app.generator.cania.logic/logic-01}}})
