(ns app.generator.logic-one
  (:require
    [selmer.parser :as selmer]
    [clojure.string :as cs]))

(def dir "resources/problem-template/logic-one/")

(defn problem-one
  []
  (let [l-p1-a ["Genderuwo" "Majikan" "Ular tangga tingting"]
        l-p1-b ["Jembatan keledai" "Jembatan kedelai" "Maknyos"]
        l-p2-a ["Gocekan masno" "Rempah rengginang" "Butiran debu"]]
    (for [a l-p1-a
          b l-p1-b
          c l-p2-a]
      {:p1-a a :p1-b b :p2-a c})))

(defn generator
  [file-name]
  (let [raw (slurp (str dir file-name))
        texts (mapv #(selmer/render raw %) (problem-one))
        soals (vec (mapcat #(cs/split % #"===major-sepa===") texts))]
    soals))


