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
      [:div.container
       (into [:div.row]
             (for [{:keys [template-id creator title]} @templates]
               [:button {:on-click #(server/get-problems template-id)}
                (str "Problem name : " title "  by " creator)]))])))
