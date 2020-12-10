(ns app.generator.sabda.logic)

(defn logic-01
  []
  (for [a ["langit" "kucing" "kerupuk" "rengginang"]
        b ["berwarna biru" "berwarna coklat" "keliatan warna warni" "garing"]]
    (merge {:a a :b b}
           (let [[pa pb]
                 (rand-nth [["orang yang tingginya kurang dari 3m" "tingginya lebih dari 3m"]
                            ["kucing yang beratnya kurang dari 1 ton" "beratnya ngga kurang dari 1 ton"]])]
             {:pa pa :pb pb}))))

(defn math-01
  []
  (loop [i 0 res []]
    (if (> i 50)
      res
      (let [resi (let [a [2 3 4 5]
                       b [6 7 8 9 10 11 12]
                       ab (/ (rand-nth a) (rand-nth b))
                       cb (/ (rand-nth a) (rand-nth b))
                       nab (numerator ab)
                       ncb (numerator cb)
                       dab (denominator ab)
                       dcb (denominator cb)]
                   {:a  ab
                    :b  cb
                    :pb (* ab cb)
                    :p1 (+ ab cb)
                    :p2 (/ (* nab ncb)
                           (+ dab dcb))
                    :p3 (/ (+ nab ncb)
                           (+ dab ncb))
                    :p4 (* ab (/ 1 cb))
                    :nab nab
                    :ncb ncb
                    :dab dab
                    :dcb dcb})]
        (recur (inc i) (conj res resi))))))


