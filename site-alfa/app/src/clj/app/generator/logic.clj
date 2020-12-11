(ns app.generator.logic)

(defn one
  []
  (let [l-p1-a ["Genderuwo" "Majikan" "Ular tangga tingting"]
        l-p1-b ["Jembatan keledai" "Jembatan kedelai" "Maknyos"]
        l-p2-a ["Gocekan masno" "Rempah rengginang" "Butiran debu"]]
    (for [a l-p1-a
          b l-p1-b
          c l-p2-a]
      {:p1-a a :p1-b b :p2-a c})))




