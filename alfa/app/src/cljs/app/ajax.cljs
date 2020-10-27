(ns alfa.ajax
  (:require
    [re-frame.core :as re]
    [alfa.utils :as u :refer [info re-render-mathjax]]
    [ajax.core :as ajax :refer [GET POST ajax-request]]
    [ajax.edn :as edn]))

(defn get-cg-details
  [cg-id]
  (->> {:uri           (str "/api/cg-details/" cg-id)
        :handler       (fn [[_ data]]
                         (re/dispatch [:set-cg-details data]))
        :error-handler (fn [[_ msg]] (set! (.-location js/window) "/"))}
       (merge (u/ajax-edn :get))
       ajax-request))

(defn get-cg-details-by-child
  [cg-id]
  (->> {:uri           (str "/api/cg-details-by-child/" cg-id)
        :handler       (fn [[_ data]]
                         (re/dispatch [:set-cg-details data]))
        :error-handler (fn [[_ msg]] (set! (.-location js/window) "/"))}
       (merge (u/ajax-edn :get))
       ajax-request))

(defn get-container-details
  [con-id]
  (->> {:uri           (str "/api/container-details/" con-id)
        :handler       (fn [[_ data]]
                         (re/dispatch [:set-container-details data]))
        :error-handler (fn [[_ msg]] (set! (.-location js/window) "/"))}
       (merge (u/ajax-edn :get))
       ajax-request))

(defn update-all
  "Update all content by sending a request for update to the server."
  []
  (->> {:uri           "/api/update-content"
        :handler       (fn [[_ data]]
                         (js/alert "All data are succesfully updated! huray...")
                         (re/dispatch [:set-cg-details data]))
        :error-handler (fn [[_ msg]] (set! (.-location js/window) "/"))}
       (merge (u/ajax-edn :get))
       ajax-request))

(defn regenerate-zp-problem
  "Asking the server to regenerate a zp-problem"
  [zpro-id]
  (->> {:uri           (str "/api/generate-zp-problem/" zpro-id)
        :handler       (fn [[_ data]]
                         (re/dispatch [:set-zp-problem data]))
        :error-handler (fn [[_ msg]] (set! (.-location js/window) "/"))}
       (merge (u/ajax-edn :get))
       ajax-request))

(defn regenerate-old-problem
  "Asking the server to regenerate a old-problem"
  [op-id]
  (->> {:uri           (str "/api/generate-old-problem/" op-id)
        :handler       (fn [[_ data]]
                         (re/dispatch [:set-old-problem data]))
        :error-handler (fn [[_ msg]] (set! (.-location js/window) "/"))}
       (merge (u/ajax-edn :get))
       ajax-request))


