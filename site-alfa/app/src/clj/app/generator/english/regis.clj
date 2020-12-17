(ns app.generator.english.regis
  (:require [app.generator.english.grammar]))

(def register
  [{:folder "english"
    :file "grammar-01.html"
    :gen-fn app.generator.english.grammar/grammar-01}
   {:folder "english"
    :file "grammar-02.html"
    :gen-fn app.generator.english.grammar/grammar-02}])
