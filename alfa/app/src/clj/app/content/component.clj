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

(declare grab-produce)

(defrecord Content [source]
  component/Lifecycle
  (start [this]
    (let [all-makers (regis/soal-map)
          problems (grab-produce source all-makers)]
      (pres problems)
      (assoc this :problems problems)))
  (stop [this]
    (dissoc this :problems)))

(defn make [content-config]
  (map->Content content-config))

(defn grab-produce
  "Grabbing problems based on source & registered problems"
  [source all-makers]
  (let [reference (->> (grabber/grab source all-makers)
                       (map :problems)
                       (apply concat)
                       vec)]
    {:list reference
     :data (mapv grabber/produce reference)}))




