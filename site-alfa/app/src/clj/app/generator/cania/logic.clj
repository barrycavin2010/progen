(ns app.generator.cania.logic)

(defn logic-01
  []
  (for [a ["pulang kampung" "baru kerja" "udah lama kerja" "baru jadi maba" "ikut internship"]
        b ["mendapat THR" "mendapat cuti" "naik gaji" "dapet upah minimal"]
        c ["Steven" "Calvin" "Oncom" "Ketoprak" "Firmino"]]
    {:a a :b b :c c}))


