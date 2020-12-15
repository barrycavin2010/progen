(ns app.generator.sabda.ekon)

(def glossary
  [{:term       "Inflasi"
    :definition "Suplai uang yang meningkat lebih cepat dari suplai goods/services, sehingga terjadi kenaikan harga-harga"}
   {:term       "Devaluasi"
    :definition "Tindakan untuk 'menurunkan' nilai mata uang"}
   {:term       "Depresiasi"
    :definition "Fenomena ketika suatu mata uang mengalami penurunan nilai, biasanya relatif terhadap mata uang asing regional/global"}
   {:term       "Apreasiasi"
    :definition "Fenomena ketika suatu mata uang mengalami kenaikan nilai, biasanya relatif terhadap mata uang asing regional/global"}
   {:term       "Ceteris paribus"
    :definition "Kondisi dimana semua variable lain selain yang diukur akan sama"}])

(defn ekon-01
  []
  (for [terms glossary
        which-one [:term :definition]]
    (condp = which-one
      :term
      (let [mapi {:istilah (:term terms)
                  :pb      (:definition terms)
                  :which-one "Mana definisi paling tepat untuk istilah berikut"}
            sisa (remove #(= (:term terms) (:term %)) glossary)]
        (merge mapi
               (zipmap [:p1 :p2 :p3 :p4] (mapv :definition sisa))
               {:terms glossary}))
      :definition
      (let [mapi {:istilah (:definition terms)
                  :pb      (:term terms)
                  :which-one "Mana istilah paling tepat untuk hal berikut "}
            sisa (remove #(= (:term terms) (:term %)) glossary)]
        (merge mapi
               (zipmap [:p1 :p2 :p3 :p4] (mapv :term sisa))
               {:terms glossary})))))
