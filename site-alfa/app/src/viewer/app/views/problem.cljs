(ns app.views.problem
  (:require
    [app.utils :as u]
    [re-frame.core :as re]
    [app.subs :as subs]))

(defn problem-panel
  []
  (fn []
    (let [problems (re/subscribe [:subs-data-problems])
          countprobs (fn [] (js/console.log @problems))]
      (js/setTimeout #(countprobs) 3000)
      [:div.container
       [:button {:on-click #(re/dispatch [:event-set-main-panel :panel-templates])}
        "Back to templates"]
       (into [:div.row]
             (for [{:keys [nomer soal bahas]}
                   (-> #(assoc %2 :nomer (inc %1))
                       (map-indexed @problems)
                       reverse)]
               [:div
                [:h4 (str "Soal : " nomer)]
                [:h6 {:dangerouslySetInnerHTML
                      {:__html (:soal-text soal)}}]
                (into [:div]
                      (for [[k v] (map #(do [%1 %2]) ["A" "B" "C" "D" "E" "F"] (map second (:options soal)))]
                        [:div (str k ". ") v]))
                [:br]
                [:h5 (str "Pembahasan : ")]
                [:h6 {:dangerouslySetInnerHTML
                      {:__html bahas}}]]))])))
