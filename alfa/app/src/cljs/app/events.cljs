(ns app.events
  (:require
    [re-frame.core :as re]
    [app.db :as db]
    [app.utils :as u :refer [info re-render-mathjax]]
    [ajax.core :as ajax :refer [GET POST ajax-request]]
    [ajax.edn :as edn]
    [app.ajax :as server]))

(re/reg-event-db
  ::initialize-db
  (fn [_ _] db/default-db))

(def set-main-panel
  (fn [db [_ main-panel]]
    (assoc db :main-panel main-panel)))

(re/reg-event-db :set-main-panel set-main-panel)











