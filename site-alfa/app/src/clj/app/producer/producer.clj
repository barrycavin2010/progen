(ns app.producer.producer
  (:require
    [selmer.parser :as selmer]
    [clojure.string :as cs]
    [app.utils :refer :all]
    [clojure.edn :as edn]))

(declare process-one-soal)

(defn generate
  [{:keys [meta gen-fn soal bahasan filename]}]
  (let [gen-data (gen-fn)
        injected-soals (mapv #(selmer/render soal %) gen-data)
        injected-bahasans (mapv #(selmer/render bahasan %) gen-data)
        maps (for [i (range (count gen-data))]
               (merge (dissoc meta :generated?)
                      {:problem-id (uuid)}))
        soals (mapv process-one-soal injected-soals)]
    (println filename " : " (count gen-data))
    (-> #(do {:soal %1 :bahas %2 :meta %3})
        (mapv soals injected-bahasans maps))))

(defn- process-one-option
  [ikey option]
  (let [[anskey text] (cs/split option #"::")]
    [(edn/read-string anskey) text ikey]))

(defn- process-one-soal
  [soal-string]
  (let [[text options] (cs/split soal-string #"==options==")
        the-options (-> (cs/trim options)
                        (cs/split #"==")
                        (shuffle))
        processed-options (map-indexed process-one-option the-options)
        [_ _ ikey] (first (filter #(true? (first %)) processed-options))]
    {:soal-text text
     :options   (vec processed-options)
     :jawaban   (["A" "B" "C" "D" "E" "F" "G" "H" "I" "J"] ikey)}))
