(ns app.views.template
  (:require
    [app.utils :as u]
    [re-frame.core :as re]
    [app.ajax :as server]
    [app.subs :as subs]))

(defn template-panel
  []
  (fn []
    (let [templates (re/subscribe [:subs-data-templates])]
      [:div.container-fluid
       [:div.row
        [:div.col-4]
        [:div.col-4 [:h4 (:pesan @templates)]]
        [:div.col-4]]
       [:div.row
        [:div.col-4]
        [:div.col-4
         [:button.btn.btn-primary "Primary"]]
        [:div.col-4
         [:button.btn.btn-success "Success"]]]])))
