(ns app.content.component
  (:require
    [com.stuartsierra.component :as component]
    [app.content.register :as regis]
    [app.content.grabber :as grabber]
    [app.utils :refer :all]))

(comment
  [{:folder   "cania"
    :problems [{:problem-name "logic-01"
                :file   "logic-01.html"
                :gen-fn app.generator.cania.logic/logic-01}
               {:problem-name "logic-02"
                :file   "logic-02.html"
                :gen-fn app.generator.cania.logic/logic-01}]}
   {:folder   "sabda"
    :problems [{:problem-name "logic-01"
                :file   "logic-01.html"
                :gen-fn app.generator.sabda.logic/logic-01}]}])

(defrecord Content [source]
  component/Lifecycle
  (start [this]
    (let [all-makers (regis/soal-map)
          all-data (grabber/grab source all-makers)
          problems (->> (map :problems all-data)
                        (apply concat)
                        vec)]
      (pres problems)
      (assoc this :problems problems)))
  (stop [this]
    (dissoc this :problems)))

(defn make [content-config]
  (map->Content content-config))




