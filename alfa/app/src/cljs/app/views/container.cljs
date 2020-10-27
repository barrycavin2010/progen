(ns alfa.views.container
  (:require-macros [reagent.interop :refer [$ $!]])
  (:require
    [cljs-react-material-ui.core :as ui]
    [cljs-react-material-ui.icons :as ic]
    [cljs-react-material-ui.reagent :as rui]
    [alfa.utils :as u]
    [reagent.ratom :as ra]
    [reagent.core :as rc]
    [reagent.dom :as rdom]
    [re-frame.core :as re]
    [alfa.themes :refer [zenius-theme]]
    [alfa.views.playlist :as pl]
    [alfa.ajax :as server]))

(defn title-panel
  [active-cg container]
  (fn [active-cg contanier]
    [:div.row
     [:div.columns.six
      [rui/paper
       [rui/list
        [rui/list-item
         {:primaryText (str (:title active-cg) " Click here to back")
          :on-click    #(server/get-cg-details (:id active-cg))}]]]]
     [:div.columns.six
      [rui/paper
       [rui/list
        [rui/list-item
         {:primaryText (:title container)}]]]]]))

(defn playlist-panel
  [playlist]
  (fn [playlist]
    [rui/paper
     (into [rui/list]
           (for [[e elmt] (map-indexed #(do [%1 %2]) playlist)]
             [rui/list-item
              {:primary-text (str "Type " e " " (:type elmt))
               :on-click     #(re/dispatch [:set-now-playing (:sequence elmt)])}]))]))

(defn now-playing-panel
  [now-playing]
  (fn [now-playing]
    [rui/paper [pl/main-panel now-playing]]))

(defn container-panel
  [playlist now-playing]
  (fn [playlist now-playing]
    [:div.row
     [:div.columns.three
      [playlist-panel playlist]]
     [:div.columns.nine
      [now-playing-panel now-playing]]]))

(defn main-panel
  []
  (let [active-cg (re/subscribe [:active-cg])
        container (re/subscribe [:container])
        playlist (re/subscribe [:playlist])
        now-playing (re/subscribe [:now-playing])]
    (fn []
      [:div.container
       [title-panel @active-cg @container]
       [:br] [:br]
       [container-panel @playlist @now-playing]])))
