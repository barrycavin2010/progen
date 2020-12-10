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
  [{:keys [file gen-fn]}]
  (let [raw (-> (slurp file)
                (cs/replace #"\n" ""))
        [meta soal bahas] (cs/split raw #"==sepa==")
        data {:meta    (edn/read-string meta)
              :soal    soal
              :bahasan bahas
              :gen-fn  gen-fn}]
    (merge data (producer/gen-inject data))))

(comment

  (defn load-files
    "Load the files of a soal directory
    Convention: text.html for the soal-bahasan and meta.edn for the meta data"
    [{:keys [source dir]}]
    (let [dir (str source "/" dir "/")]
      {:text (slurp (str dir "text.html"))
       :meta (cslurp (str dir "meta.edn"))}))

  (defn sepa-soals
    "Separate each soal-bahas in a template"
    [text]
    (cs/split text #"===major-sepa==="))

  (defn sepa-soal-bahasan
    "Separate the soal part and bahasan part of a soal template"
    [text]
    (zipmap [:soal :bahasan] (cs/split text #"===sepa-soal-bahas===")))

  (defn inject-data
    "Inject the data into the templates"
    [soal-map soal-bahasans]
    (let [data (vec ((:gen-fn soal-map)))]
      (for [{:keys [soal bahasan]} soal-bahasans
            datum data]
        {:soal    (selmer/render soal datum)
         :bahasan (selmer/render bahasan datum)})))

  (defn generate
    [{:keys [source max-view soal-choice] :as content-map}]
    (if-let [soal-map (get (reg/soal-map) soal-choice)]
      (let [files (load-files (merge content-map soal-map))
            soals (sepa-soals (:text files))
            soal-bahasans (mapv sepa-soal-bahasan soals)]
        (time (->> (inject-data soal-map soal-bahasans)
                   shuffle
                   (take max-view))))
      (error "No soal with that name in our register"))))
