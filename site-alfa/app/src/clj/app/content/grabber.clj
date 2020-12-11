(ns app.content.grabber
  (:require
    [clojure.edn :as edn]
    [clojure.string :as cs]
    [app.content.producer :as producer]
    [app.utils :refer :all]))

(defn grab
  [source makers]
  (for [maker makers]
    (let [creator (:folder maker)
          folder (str source creator "/")
          problems (-> #(update % :file (fn [x] (str folder x)))
                       (map (:problems maker)))]
      {:nama     creator
       :folder   folder
       :problems (map #(assoc % :creator creator) problems)})))

(defn produce
  [{:keys [creator file gen-fn]}]
  (let [raw (-> (slurp file)
                (cs/replace #"\n" ""))
        [meta soal bahas] (cs/split raw #"==sepa==")
        data {:meta    (-> (edn/read-string meta)
                           (assoc :creator creator)
                           (assoc :template-id (uuid)))
              :soal    soal
              :bahasan bahas
              :gen-fn  gen-fn}]
    (-> (merge data (producer/gen-inject data))
        (dissoc :soal :bahasan :gen-fn))))
