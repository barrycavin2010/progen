(ns app.generator.logic.logic)

(defn logic-01
  []
  (->> (for [a ["langit" "kucing" "kerupuk" "rengginang"]
             b ["berwarna biru" "berwarna coklat" "keliatan warna warni" "garing"]]
         (merge {:a a :b b}
                (let [[pa pb]
                      (rand-nth [["orang yang tingginya kurang dari 3m" "tingginya lebih dari 3m"]
                                 ["kucing yang beratnya kurang dari 1 ton" "beratnya ngga kurang dari 1 ton"]])]
                  {:pa pa :pb pb})))
       shuffle
       (take 20)))

(defn logic-02
  []
  (->> (for [a ["pulang kampung" "baru kerja" "udah lama kerja" "baru jadi maba" "ikut internship"]
             b ["mendapat THR" "mendapat cuti" "naik gaji" "dapet upah minimal"]
             c ["Steven" "Calvin" "Oncom" "Ketoprak" "Firmino"]]
         {:a a :b b :c c})
       shuffle
       (take 20)))




