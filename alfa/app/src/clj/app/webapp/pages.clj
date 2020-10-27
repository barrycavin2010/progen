(ns alfa.webapp.pages
  (:require
    [clojure.pprint :refer [pprint]]
    [selmer.parser :as selmer :refer [render-file]]
    [alfa.utils :refer :all]
    [me.raynes.fs :as fs]))

(selmer/cache-off!)

(defn file
  [filename]
  (str "template/" filename ".html"))

(defn home [] (render-file (file "index") {}))

(defn small [resources]
  "Html template producing static for the mobile version.
  It accepts the resources to be included into the html header."
  (let [js-res (->> resources
                    (filter #(= ".js" (fs/extension %)))
                    (mapv #(str "/resources/" %)))
        css-res (->> resources
                     (filter #(= ".css" (fs/extension %)))
                     (mapv #(str "/resources/" %)))]
    (render-file (file "mobile")
                 {:js-res  (distinct js-res)
                  :css-res (distinct css-res)})))

(defn large [resources]
  "Html template producing static for the mobile version.
  It accepts the resources to be included into the html header."
  (let [js-res (->> resources
                    (filter #(= ".js" (fs/extension %)))
                    (mapv #(str "/resources/" %)))
        css-res (->> resources
                     (filter #(= ".css" (fs/extension %)))
                     (mapv #(str "/resources/" %)))]
    (render-file (file "desktop")
                 {:js-res  (distinct js-res)
                  :css-res (distinct css-res)})))
