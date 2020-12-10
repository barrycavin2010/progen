(ns app.content.producer
  (:require
    [selmer.parser :as selmer]
    [clojure.string :as cs]
    [app.utils :refer :all]
    [clojure.edn :as edn]))

(declare process-one-soal)

(defn gen-inject
  [{:keys [meta soal bahasan gen-fn]}]
  (let [gen-data (gen-fn)
        injected-soals (mapv #(selmer/render soal %) gen-data)
        injected-bahasans (mapv #(selmer/render bahasan %) gen-data)
        soals (mapv process-one-soal injected-soals)]
    {:gen-data      gen-data
     :soal-bahasans (-> #(do {:soal %1 :bahas %2})
                        (mapv soals injected-bahasans))}))

(defn- process-one-option
  [option]
  (let [[anskey text] (cs/split option #"::")]
    [(edn/read-string anskey) text]))

(defn process-one-soal
  [soal-string]
  (let [[text options] (cs/split soal-string #"==options==")
        the-options (cs/split options #"==")]
    {:soal-text text
     :options   (mapv process-one-option the-options)}))
