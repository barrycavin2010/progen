(ns app.views.soal
  (:require
    [app.utils :as u]
    [re-frame.core :as re]
    [app.subs :as subs]))

(defn soal-panel
  []
  (fn []
    (let [soals (re/subscribe [:subs-data-soals])]
      [:div.container
       (into [:div.row]
             (-> #(do [:div.row
                       [:div.row [:h4 (str "Soal no : " %1)]]
                       [:div.row {:dangerouslySetInnerHTML {:__html (:soal %2)}}]
                       [:h5 "Pembahasan:"]
                       [:div.row {:dangerouslySetInnerHTML {:__html (:bahasan %2)}}]])
                 (map (range 1 (inc (count @soals))) @soals)))])))
