(ns app.subs
  (:require [re-frame.core :as re]))

(re/reg-sub
  ::main-panel
  (fn [db]
    (:main-panel db)))




