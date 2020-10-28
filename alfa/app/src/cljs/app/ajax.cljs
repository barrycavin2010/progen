(ns app.ajax
  (:require
    [re-frame.core :as re]
    [app.utils :as u :refer [info re-render-mathjax]]
    [ajax.core :as ajax :refer [GET POST ajax-request]]
    [ajax.edn :as edn]))

(defn get-initial-content
  "Update all content by sending a request for update to the server."
  []
  (->> {:uri           "/api/get-content"
        :handler       (fn [[_ data]]
                         (re/dispatch [:event-set-soals data]))
        :error-handler (fn [[_ msg]] (set! (.-location js/window) "/"))}
       (merge (u/ajax-edn :get))
       ajax-request))


