(ns alfa.views.old-problem
  (:require [alfa.utils :refer [re-render-mathjax]]))

(def alphabet "ABCDEFGHIJKLMNOPQRSTUVWXYZ")

(defn main-panel
  [i problem]
  [:div.problem-app
   [:div#soal
    (when (:soaljmk problem)
      (let [soaljmk (:soaljmk problem)
            c (count soaljmk)
            soals (map vector
                       (range 0 c)
                       soaljmk)]
        (for [[i s] soals]
          ^{:key i}
          [:p s])))
    (when (:soal problem)
      (let [soal (-> (:soal problem))]
        (if (string? soal)
          [:p soal]
          (let [c (count soal)
                soals (map vector
                           (range 0 c)
                           soal)]
            (for [[i s] soals]
              ^{:key i}
              [:p s])))))]
   [:div#choices
    (let [pilihan (:pilihan problem)]
      (for [[i p] (->> pilihan
                       (map vector alphabet))]
        ^{:key i}
        [:button
         {:on-click #(.alert js/window (str "JAWABAN ELU ...."
                                            (if (= (str i) (:jawaban problem))
                                              "BENER, HOKIE LU JING!"
                                              "SALAH, TOLOL LU JING!")))}
         [:p (str i ". " p)]]))]
   [:div#pic
    (when (:img problem)
      [:img {:src (str (:img problem))}])
    (when (:imgjmk problem)
      [:div
       (let [imgsrc (-> problem :imgjmk :add)]
         (for [i (range 0 (-> problem :imgjmk :jum))]
           ^{:key i}
           [:img {:src (str imgsrc)}]))])
    (when (:imgmix problem)
      (let [mix (:imgmix problem)
            c (count mix)]
        (for [[i src] (map vector
                           (range 0 c)
                           mix)]
          ^{:key i}
          [:img {:src (str (:add src))}])))
    (when (:pictures problem)
      (let [idc (str "myCanvas" i)]
        [:div
         [:canvas {:id idc :width 400 :height 200}]]))
    (when (:grafik problem)
      (let [idc (str "myCanvas" i)]
        [:div
         [:canvas {:id idc :width 400 :height 320}]]))
    (when (:tabel problem)
      (let [table-data (->> problem :tabel :data)
            table-map (->> problem :tabel)]
        [:div
         [:table
          [:tr
           [:th (str (:var1 table-map))]
           [:th (str (:var2 table-map))]]
          (for [s table-data]
            ^{:key (str s)}
            [:tr
             [:td (str (:x s))]
             [:td (str (:y s))]])]]))]
   (let [_ (when (:pictures problem)
             (js/setTimeout
               #(let [{:keys [pictures backimg kx ky]} problem]
                 (js/initgambar (str "myCanvas" i) backimg kx ky (clj->js (vec pictures))))
               (+ 1000 (* 1000 i))))
         _ (when (:grafik problem)
             (js/setTimeout
               #(let [{:keys [pair var1 var2 max1 max2 datax datay]} (:grafik problem)]
                 (js/initgrafik (str "myCanvas" i) pair var1 var2 max1 max2 (clj->js datax) (clj->js datay)))
               (+ 1000 (* 1000 i))))]
     [:hr])
   (re-render-mathjax)])
