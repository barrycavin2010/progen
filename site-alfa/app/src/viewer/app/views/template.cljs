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
             (for [{:keys [template-id filename]} @templates]
               [:button {:on-click #(do (server/get-problems template-id)
                                        (js/console.log template-id))}
                (str "Problem name : " filename)]))])))
