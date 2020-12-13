(ns app.producer.register
  (:require
    [app.generator.cania.regis :as cania]
    [app.generator.sabda.regis :as sabda]))

(defn soal-map
  "Register each folder"
  []
  (vector (sabda/register)
          (cania/register)))


