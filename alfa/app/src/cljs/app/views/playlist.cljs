(ns alfa.views.playlist
  (:require-macros [reagent.interop :refer [$ $!]])
  (:require
    [cljs-react-material-ui.core :as ui]
    [cljs-react-material-ui.icons :as ic]
    [cljs-react-material-ui.reagent :as rui]
    [alfa.utils :as u]
    [reagent.ratom :as ra]
    [reagent.core :as rc]
    [alfa.ajax :as server]
    [re-frame.core :as re]
    [alfa.themes :refer [zenius-theme]]
    [alfa.views.old-problem :as op]))

(defn video-panel
  []
  (fn []
    (let [now-playing (re/subscribe [:now-playing])]
      (if (= :error (get-in @now-playing [:data :id]))
        [:div [:p "Videonya emang belom kecopy aja"]]
        [:div.container
         [:h4 (str "This is supposed to be a video with title " (get-in @now-playing [:data :nama]))]
         [:h5 [:a {:href   (str "/video/" (get-in @now-playing [:data :filename]))
                   :target "_blank"}
               "This is the link to the video"]]
         [:video {:width "640" :height "480" :controls true :autoPlay true}
          [:source {:src  (str "/video/" (get-in @now-playing [:data :filename]))
                    :type "video/mp4"}]]]))))

(defn znet-problem-panel
  [now-playing]
  (if (= :error (get-in now-playing [:data :id]))
    [:div [:p "Somehow imagenya nggak ke copy"]]
    [:div.container
     [:img {:src (str "/znet_problem/" (get-in now-playing [:data :image]))}]]))

(defn notes-panel
  [now-playing]
  (fn [now-playing]
    (if (= :error (get-in now-playing [:data :id]))
      [:div [:p "Somehow datanya ngga ada"]]
      (do (js/setTimeout #(try (js/eval (get-in now-playing [:data :script]))
                               (catch js/Error e)) 30)
          (js/setTimeout u/re-render-mathjax 30)
          [:div.container
           {:dangerouslySetInnerHTML {:__html (get-in now-playing [:data :text])}}]))))

(defn old-problem-panel
  [now-playing]
  (fn [now-playing]
    (if (= :error (get-in now-playing [:data :id]))
      [:div [:p "Somehow datanya ngga ada"]]
      (do (js/setTimeout u/re-render-mathjax 30)
          [:div.container
           [:center
            [:div [rui/raised-button
                   {:label     "Regenerate problem"
                    :secondary true
                    :on-click  #(-> (:id now-playing)
                                    server/regenerate-old-problem)}]]
            [:hr]
            [op/main-panel 1 (:data now-playing)]]]))))

(declare zp-set-single zp-set-multiple
         zp-single zp-multiple zp-causal)

(defn zp-problem-viewer
  [data]
  (if (:set? data)
    (condp = (:type data)
      :single [zp-set-single data]
      :multiple [zp-set-multiple data]
      :zp-problem [:div [:p "zp-problem error"]])
    (condp = (:type data)
      :single [zp-single data]
      :multiple [zp-multiple data]
      :causal [zp-causal data]
      :zp-problem [:div [:p "zp-problem error"]])))

(defn zp-problem-panel
  []
  (let [now-playing (re/subscribe [:now-playing])]
    (fn []
      (if (= :error (get-in @now-playing [:data :id]))
        [:div [:p "Error nih pas digenerate"]]
        (do (js/setTimeout #(try (js/eval (get-in @now-playing [:data :script]))
                                 (catch js/Error e)) 30)
            (js/setTimeout u/re-render-mathjax 30)
            [:div.container
             [:center [:div [rui/raised-button
                             {:label     "Regenerate problem"
                              :secondary true
                              :on-click  #(-> (get-in @now-playing [:data :id])
                                              server/regenerate-zp-problem)}]]]
             [:hr]
             [zp-problem-viewer (get-in @now-playing [:data])]])))))

(defn main-panel
  [now-playing]
  (condp = (:type now-playing)
    :video [video-panel]
    :znet-problem [znet-problem-panel now-playing]
    :zp-problem [zp-problem-panel]
    :notes [notes-panel now-playing]
    :old-problem [old-problem-panel now-playing]
    [:div [:h3 "Mending kita mabu2xan"]]))

;;--------------- zp problem viewers ----------------------

(defn zp-single
  [data]
  (fn [data]
    (let [sequ (mapv str "ABCDEFGHIJKLMNOPQRSTUV")]
      [:div.container
       [:div.row {:dangerouslySetInnerHTML {:__html (get-in data [:soal :text])}}]
       (into [:div.row]
             (for [[idx inf ch] (map-indexed #(do [%1 (first %2) (second %2)])
                                             (get-in data [:soal :choices]))]
               [:div
                [:p (str (sequ idx) " " (str inf))
                 [:span {:dangerouslySetInnerHTML
                         {:__html ch}}]]]))
       [:div.row [:p (str "Jawabannya " (:answer data))]]])))

(defn zp-multiple
  [data]
  (fn [data]
    (let [sequ (mapv str "ABCDEFGHIJKLMNOPQRSTUV")]
      [:div.container
       [:div.row {:dangerouslySetInnerHTML {:__html (get-in data [:soal :text])}}]
       (into [:div.row]
             (for [[idx inf ch] (map-indexed #(do [%1 (first %2) (second %2)])
                                             (get-in data [:soal :choices]))]
               [:div
                [:p (str (sequ idx) " " (str inf))
                 [:span {:dangerouslySetInnerHTML
                         {:__html ch}}]]]))
       [:div.row [:p (str "Jawabannya " (:answers data))]]])))

(defn zp-set-single
  [data]
  (fn [data]
    (let [pairs (map #(do [{:text    (:text %)
                            :choices (:choices %)}
                           %2])
                     (:soals data) (:answers data))
          res (mapv #(zipmap [:soal :answer] %) pairs)]
      [:div.row
       [:div {:dangerouslySetInnerHTML
              {:__html (:header data)}}]
       (into [:div.row]
             (for [soal res]
               [zp-single soal]))])))

(defn zp-set-multiple
  [data]
  (fn [data]
    (let [pairs (map #(do [{:text    (:text %)
                            :choices (:choices %)}
                           %2])
                     (:soals data) (:answers data))
          res (mapv #(zipmap [:soal :answers] %) pairs)]
      [:div.row
       [:div {:dangerouslySetInnerHTML
              {:__html (:header data)}}]
       (into [:div.row]
             (for [soal res]
               [zp-multiple soal]))])))

(defn zp-causal
  [data]
  (fn [data]
    [:div.row [:p {:dangerouslySetInnerHTML {:__html (:sebab data)}}]
     [:br] [:br]
     [:h5 "sebab"]
     [:p {:dangerouslySetInnerHTML {:__html (:akibat data)}}]
     [:br] [:br]
     [:p (str "Jawaban " (:answer data))]]))


