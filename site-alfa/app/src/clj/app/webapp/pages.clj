(ns app.webapp.pages
  (:require
    [clojure.pprint :refer [pprint]]
    [selmer.parser :as selmer :refer [render-file]]
    [app.utils :refer :all]))

(selmer/cache-off!)

(defn file
  [filename]
  (str "template/" filename ".html"))

(defn home [] (render-file (file "index") {}))

(defn large [content]
  "Html template producing static for the mobile version.
  It accepts the resources to be included into the html header."
  (render-file (file "desktop")
               content))
