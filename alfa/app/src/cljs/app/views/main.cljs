(ns alfa.views.main
  (:require-macros [reagent.interop :refer [$! $]])
  (:require
    [cljsjs.material-ui]
    [cljs-react-material-ui.core :as ui]
    [cljs-react-material-ui.icons :as ic]
    [cljs-react-material-ui.reagent :as rui]
    [alfa.utils :as u]
    [reagent.ratom :as ra]
    [reagent.core :as rc]
    [re-frame.core :as re]
    [alfa.views.cgroup :as cg]
    [alfa.themes :refer [zenius-theme]]
    [alfa.views.container :as container]
    [alfa.ajax :as server]))

(defn waiter-panel
  "Just a waiting progress bar."
  []
  (fn []
    (->> [[:center
           [:h5 "Please be patient, the content you're requesting may or may not appear"]
           [:br] [:br]
           [rui/circular-progress {:size 1.5}]]]
         (concat [:div.container]
                 (repeat 2 [:br]))
         (vec))))

(defn show-main-panel
  "This is the main panel slot for all other parts of the app after app-bar part."
  [main-panel]
  (fn [main-panel]
    (condp = main-panel
      :waiter [waiter-panel]
      :content-group [cg/main-panel]
      :container [container/main-panel]
      [waiter-panel])))

(defn footer []
  [:div.container
   [:br]
   [:hr]
   [:center [:h5 "Copyright PT Zenius Education"]]])

(defn body-controller
  []
  (let [main-panel (re/subscribe [:main-panel])]
    (fn []
      [show-main-panel @main-panel])))

(defn header
  []
  (fn []
    [:center
     [rui/app-bar
      {:title              "Zenius Prestasi"
       :icon-element-left  (ui/icon-button
                             {:on-click #(do (server/get-cg-details "top")
                                             (re/dispatch [:set-main-panel :waiter]))}
                             (ic/action-home))
       :icon-element-right (ui/icon-button
                             {:on-click #(do (js/alert "Lagi diupdate, jangan ngapa2xin sebelom ada notif lagi!!!!\nBoleh sih kalo cuma close/click ok alert ini...")
                                             (re/dispatch [:set-main-panel :waiter])
                                             (server/update-all))}
                             (ic/action-autorenew))
       :on-click           #(do (server/get-cg-details "top")
                                (re/dispatch-sync [:set-main-panel :main]))}]]))

(defn main-page
  []
  [rui/mui-theme-provider
   {:mui-theme (ui/get-mui-theme zenius-theme)}
   [:div
    [header]
    [body-controller]
    [footer]]])

