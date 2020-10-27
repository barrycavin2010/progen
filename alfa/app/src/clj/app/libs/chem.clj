(ns alfa.libs.chem
  (:require [clojure.string :as cs]
            [alfa.libs.helper :as helper]
            [alfa.libs.utils :as u]
            ))



(defn kelarutan' [coll]
  (cond
    (= (coll 2) "S2") (Math/sqrt (coll 1))
    (= (coll 2) "4S3") (Math/pow (/ (coll 1) 4) 1/3)
    (= (coll 2) "27S4") (Math/pow (/ (coll 1) 27) 1/4)
    :else "eror cuk"
    ))

;;(splitpow 0.0001)
;;{:x 1.0, :y -4}
(defn splitpow [x]
  (cond (= x 0) {:x 0 :y 0}
        (= x 1) {:x 1 :y 0}
        (< x 1) (loop [init x y 0]
                    (if (>= init 1) {:x init :y (* -1 (dec y))}
                                    (recur (* x (Math/pow 10 y)) (inc y))))
        (> x 1) (loop [init x y 0]
                  (if (<= init 1) {:x init :y (dec y)}
                                  (recur (/ x (Math/pow 10 y)) (inc y))))))

(defn p10 [x]
  (u/rep-comma (str "$" (if (== (int (:x (splitpow x))) (:x (splitpow x))) (int (:x (splitpow x)))
                                                                           (format "%.2f" (:x (splitpow x))))
                    " \\times 10^{" (:y (splitpow x)) "}" "$")))

;;only for (x < 1)
;;(topow 0.0001231231)
;;"1.2 $* 10^{-4}$"
(defn topow [x]
  (cond (= x 0) "eror gebs"
        (>= x 1) "eror juga gebs"
        (< x 0) "lebih eror lagi"
        :else
        ((fn [m]
           (let [a (:x m)
                 b (:y m)
                 a' (if (== (Math/round a) a) (Math/round a)
                                              (format "%.1f" a))]
             (if (= b 0) a
                         (str "$" a' " \\times 10^{" b "}$"))))
          (loop [init x y 0]
            (if (>= init 1) {:x init :y (* -1 (dec y))}
                            (recur (* x (Math/pow 10 y)) (inc y)))))))


(defn phasam [x]
  (cond (= x 0) "eror"
        (>= x 1) "eror juga"
        (< x 0) "lebih eror lagi"
        :else
        (let [m (splitpow x)
              a (m :x)
              a' (if (== a (int a)) (int a) (format "%.1f" a))
              ph (if (== a 1) (str (* -1 (m :y)))
                              (str (* -1 (m :y)) " " "- log " a'))]
          ph)))


(defn phbasa [x]
  (cond (= x 0) "eror"
        (>= x 1) "eror juga"
        (< x 0) "lebih eror lagi"
        :else
        (let [m (loop [init x y 0]
                  (if (>= init 1) {:x init :y (* -1 (dec y))}
                                  (recur (* x (Math/pow 10 y)) (inc y))))
              a (m :x)
              a' (if (== a (int a)) (int a) (format "%.1f" a))
              ph (if (== 1 a) (str (- 14 (* -1 (m :y))))
                              (str (- 14 (* -1 (m :y))) " " "+ log " a'))]
          ph)))



