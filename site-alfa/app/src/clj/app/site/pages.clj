(ns app.site.pages
  (:require
    [clojure.pprint :refer [pprint]]
    [selmer.parser :as selmer :refer [render-file]]
    [app.utils :refer :all]))

(selmer/cache-off!)

(defn file
  [filename]
  (str "template/" filename ".html"))

(defn site [producer]
  (render-file (file "site")
               producer))
