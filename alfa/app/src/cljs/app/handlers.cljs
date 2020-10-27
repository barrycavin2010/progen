(ns alfa.handlers
  (:require
    [re-frame.core :as re]
    [alfa.db :as db]
    [alfa.utils :as u :refer [info re-render-mathjax]]
    [ajax.core :as ajax :refer [GET POST ajax-request]]
    [ajax.edn :as edn]
    [alfa.ajax :as server]))

(re/register-handler
  :initialize-db
  (fn [_ _] db/default-db))

(def set-main-panel
  (fn [db [_ main-panel]]
    (assoc db :main-panel main-panel)))

(re/register-handler :set-main-panel set-main-panel)

(def set-cg-details
  (fn [db [_ data]]
    (re/dispatch [:set-main-panel :content-group])
    (assoc db :active-cg (:active-cg data)
              :cg-children (:cg-children data)
              :containers (:containers data))))

(re/register-handler :set-cg-details set-cg-details)

(def set-container-details
  (fn [db [_ data]]
    (re/dispatch [:set-main-panel :container])
    (let [new-pl (->> (map-indexed #(assoc %2 :sequence %1) (:playlist data))
                      (map-indexed #(assoc-in %2 [:data :sequence] %1)))]
      (-> (assoc db :container (assoc-in data [:playlist] new-pl))
          (assoc :playlist (vec new-pl))
          (assoc :now-playing (first new-pl))))))

(re/register-handler :set-container-details set-container-details)

(def set-zp-problem
  (fn [db [_ data]]
    (js/setTimeout #(re-render-mathjax) 30)
    (let [pl (->> (:playlist db)
                  (map-indexed #(do [%1 %2 (:data %2)]))
                  (filter #(= (:id (second %)) (:id data)))
                  first)
          new-data (-> (assoc data :sequence (:sequence (second pl)))
                       (assoc-in [:data :sequence] (:sequence (second pl))))]
      (-> (assoc db :now-playing new-data)
          (assoc-in [:playlist (:sequence (second pl))] new-data)))))

(re/register-handler :set-zp-problem set-zp-problem)

(def set-old-problem
  (fn [db [_ data]]
    (js/setTimeout #(re-render-mathjax) 30)
    (let [pl (->> (:playlist db)
                  (map-indexed #(do [%1 %2 (:data %2)]))
                  (filter #(= (:id (second %)) (:id data)))
                  first)
          new-data (-> (assoc data :sequence (:sequence (second pl)))
                       (assoc-in [:data :sequence] (:sequence (second pl))))]
      (-> (assoc db :now-playing new-data)
          (assoc-in [:playlist (:sequence (second pl))] new-data)))))

(re/register-handler :set-old-problem set-old-problem)

(def set-now-playing
  (fn [db [_ sequ]]
    (js/setTimeout #(re-render-mathjax) 30)
    (assoc db :now-playing (-> ((:playlist db) sequ)
                               (assoc-in [:data :sequence] sequ)
                               (assoc :sequence sequ)))) )

(re/register-handler :set-now-playing set-now-playing)









