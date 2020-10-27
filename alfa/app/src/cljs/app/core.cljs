(ns alfa.core
  (:require
    [reagent.core :as rc]
    [re-frame.core :as re]
    [alfa.handlers]
    [alfa.subs]
    [alfa.views.main :as view]
    [alfa.utils :as u]))

(defn mount-root []
  (rc/render [view/main-page] (u/by-id "app")))

(enable-console-print!)

(defn ^:export init []
  (re/dispatch-sync [:initialize-db])
  (alfa.ajax/get-cg-details "top")
  (re/dispatch [:set-main-panel :waiter])
  (mount-root))

