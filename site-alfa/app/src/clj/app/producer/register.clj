(ns app.producer.register
  (:require
    [app.generator.logic.regis :as logic]
    [app.generator.english.regis :as english]
    [app.generator.math.regis :as math]))

(defn soal-map
  "Register each folder"
  []
  (concat logic/register english/register math/register))


