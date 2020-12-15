(ns app.generator.math.regis
  (:require [app.generator.math.arit]))

(def register
  [{:folder "math"
    :file "arit-01.html"
    :gen-fn app.generator.math.arit/arit-01}])
