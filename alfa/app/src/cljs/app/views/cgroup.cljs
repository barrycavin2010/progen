(ns alfa.views.cgroup
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
    [alfa.ajax :as server]))

;; Transitions and animations

(defn cg-header-content
  [cg-title]
  (fn [cg-title]
    [rui/paper
     {:id "title-header"}
     [rui/list
      [rui/list-item
       {:primaryText cg-title}]]]))

(defn cg-header-panel
  "cg header with react style class"
  [title]
  (fn [title]
    [:div [cg-header-content title]]))

(defn cg-children-panel
  "Listing the cg children of the cg, special animation for entering and leaving."
  [cg-children]
  (fn [cg-children]
    (when-not (empty? cg-children)
      (->> (for [cg cg-children]
             [rui/list-item
              {:primary-text (:title cg)
               :on-click     #(server/get-cg-details (:id cg))}])
           (into [rui/list])))))

(defn containers-panel
  "Listing the containers of a cg"
  [containers]
  (fn [containers]
    (when-not (empty? containers)
      (->> (for [container containers]
             [rui/list-item
              {:primary-text (:title container)
               :on-click     #(server/get-container-details (:id container))}])
           (into [rui/list])))))

(defn back-button
  [cg-id]
  (fn [cg-id]
    (when-not (= "top" cg-id)
      [:center
       [:br] [:br]
       [rui/raised-button
        {:label    "Back"
         :on-click #(server/get-cg-details-by-child cg-id)}]])))

(defn main-panel
  "Listing all cg children for a given cg parent. The children are clickable.
   It also displays the containers of the cg, and the containers are clickable and
   triggering the container panel in the same panel slot."
  []
  (let [cg-children (re/subscribe [:cg-children])
        active-cg (re/subscribe [:active-cg])
        containers (re/subscribe [:containers])]
    (fn []
      [:div.container
       [:br]
       [:center {:id "element-header-panel"}
        [cg-header-panel (:title @active-cg)]]
       [:br]
       [rui/paper
        [cg-children-panel @cg-children]
        [rui/divider]
        [containers-panel @containers]]
       [back-button (:id @active-cg)]])))
