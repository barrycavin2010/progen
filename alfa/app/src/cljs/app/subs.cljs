(ns app.subs
  (:require [re-frame.core :as re]))

(re/reg-sub
  :subs-view-main-panel
  (fn [db]
    (:view-main-panel db)))

(re/reg-sub
  :subs-data-soals
  (fn [db]
    (:data-soals db)))




