(ns app.engines.produce
  (:require
    [selmer.parser :as selmer]
    [clojure.string :as cs]
    [me.raynes.fs :as fs]
    [app.utils :refer :all]))

(defn map-creator
  "Merging all the soals with a common meta-data from the soal template"
  [{:keys [meta soals]}]
  (mapv #(merge meta %) soals))

(defn generate
  [content-map]
  true)
