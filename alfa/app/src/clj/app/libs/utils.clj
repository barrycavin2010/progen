(ns alfa.libs.utils
  (:require [clojure.string :as cs]
            [alfa.libs.helper :as helper]
            ))

(defn capitalise [x]
    "Input a string >= 1 word, output string that capitalise only the first word. \"under the bridge\" -> \"Under the bridge\""
  (let [[a & b] (clojure.string/split x #" ")
        ax (clojure.string/capitalize (first (seq a)))
        ay (apply str (rest (seq a)))
        na (str ax ay)]
    (->> (cons na b)
         (clojure.string/join " "))))

(defn rep-comma [x]
  ;; "Input Float -> output string. 1.3 -> "1,3"
  ;;ganti titik dengan koma
  (cs/replace (str x) "." ","))

(defn penyederhanaan [& args]
    "Input numbers, output a vector consisting those numbers divide by their GCD. If GCD = 1, then output vector of initial number.
    Input 2 4 6 -> output [1 2 3]"
      (if (< 1 (count args))
        (let [pembagi (apply helper/fpb1 args)]
             (mapv #(/ % pembagi) args))
        (vec args)))

;;input recomend to be string with comma (if the number is decimal)->
;; (u/ribu "143521,12")
;; result => "143.521,12"
;; (u/ribu "1000")
;; Result => "1.000"
(defn ribu [x]
  (let [a (cs/split (rep-comma x) #",")
        b (if (= nil (second a)) ""
                                 (str "," (second a)))
        c (->> (str (first a))
               (reverse)
               (partition-all 3)
               (interpose ".")
               (map #(apply str %))
               (apply str)
               (reverse)
               (apply str))]
    (str c b)))

(defn nobigint [x]
  (if (== (int x) x) (int x) x))

(defn dec-int [x]
  (if (== x (int x)) (int x)
                     x))


(defn dec-int2 [x]
  (rep-comma (if (== x (int x)) (int x)
                                (format "%.2f" x))))

(defn nomin [x]
  (if (< x 0) (* -1 x) x))

(defn median' [coll]
  (let [coll coll
        sorted (sort coll)
        countcoll (count coll)
        nth' (/ countcoll 2)
        ]
    (if (== nth' (int nth'))
      (/ (+ (nth sorted (dec nth') ) (nth sorted nth')) 2.)
      (nth sorted (Math/round (- nth' 1.0))))
    ))