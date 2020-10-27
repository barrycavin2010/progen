(ns alfa.libs.helper
  (:require [clojure.string :as cs]
            [alfa.libs.constant :as constant]))

(defn psalah1
  "Provide wrong choices at a range
  jbenar => jumlah benar
  rng => range
  delta => differences beetwen value
  flag => true (negative number and zero not included)
  (psalah1 5 6 1 false) => [2 -1 4 10 0 7 3 9 6 1 8]
  (psalah1 5 6 1 true) => [2 4 10 7 3 9 6 1 8]"

  [jbenar rng delta flag]
  (if flag
    (->> (concat (range (- jbenar rng) jbenar delta)
                 (range (+ jbenar delta) (+ jbenar rng) delta))
          (filter #(> % 0))
          shuffle
          vec)
    (->> (concat (range (- jbenar rng) jbenar delta)
                 (range (+ jbenar delta) (+ jbenar rng) delta))
          shuffle
          vec)))

(defn psalah2
  "Provide wrong choice by multiply true choice with a number
  (psalah2 5 2 3 4 5) => [10 15 20 25] "
  ([x o1 o2 o3]
   (let [pS (distinct [(* x o1) (* x o2) (* x o3)])]
     (if (< (count pS) 3) (psalah1 x 5 1 true) (vec pS))))

  ([x o1 o2 o3 o4]
   (let [pS (distinct [(* x o1) (* x o2) (* x o3) (* x o4)])]
     (if (< (count pS) 4) (psalah1 x 6 1 true) (vec pS)))))

(defn pangkat [x y]
  (reduce * (repeat y x)))

(defn sieve [lst]
  "Find primes number in list"
  (let [rm (Math/ceil (Math/sqrt (last lst)))]
  (loop [p [],  v lst, flag false]
    (if (or flag (= v []))
        (concat p v)
        (recur
          (conj p (first v))
          (remove #(= (rem % (first v)) 0) v)
          (if (> (first v) rm) true false))))))

(defn prime? [x]
  "Wheter x is a prime or not"
  (if (= x 2) true
    (let [mr (->> (Math/sqrt x)
                  (Math/ceil))
          tp (filter #(<= % mr) constant/vprima100k)]
      (not-any? #(= (rem x %) 0) tp))))

(defn fakprima [x]
  "Prime factorization of a number"
  (if (or (prime? x) (= x 1))
    [x]
    (let [mr (Math/ceil (/ x 2.0))
          tp (filter #(<= % mr) constant/vprima100k)]
      (loop [fp [], vtp tp, vx x]
        (if (and (= (quot vx (first vtp)) 1) (= (rem vx (first vtp)) 0))
            (conj fp (first vtp))

            (let [sisa (rem vx (first vtp))
                    lp (first vtp)]
            (recur
              (if (= sisa 0) (conj fp lp) fp)
              (if (= sisa 0) vtp (rest vtp))
              (if (= sisa 0) (quot vx lp) vx)
              )))))))

 (defn power-set [col]
   "Konsepnya Himpunan Bagian"
  (reduce
   (fn [base x]
      (concat (map #(conj % x) base) base))
   [[]] col))

(defn vfaktor [coll]
  (remove #{[]}
    (distinct (power-set coll))))

(defn all_factor [x]
  "All factor of a number"
  (sort
  (cons 1
  (map #(apply * %) (vfaktor (fakprima x))))))

(defn fpb
  "Greatest common factor of two numbers"
  [x y]
  (let [ a (partition-by identity (fakprima x))
         b (partition-by identity (fakprima y))
         c (->> (concat a b)
                (group-by first)
                (filter #(= (count (val %)) 2))
                (map (fn [x] [(key x) (apply min (map count (val x)))]))
                (concat [[1 1]])
                (map #(pangkat (first %) (second %)))
                (reduce *))]
    c))

(defn kpk
  "Smallest common multiplier of two numbers"
  [x y]
  (let [ a (partition-by identity (fakprima x))
         b (partition-by identity (fakprima y))
         c (->> (concat a b)
                (group-by first)
                (map (fn [x] [(key x) (reduce max (map count (val x)))]))
                (map #(pangkat (first %) (second %)))
                (reduce *))]
    c))

(defn fpb1
  "Greatest common factor of numbers"
  [x y & more]
  (let [varg (conj more x y)
        cv (count varg)]
           (->> (map #(partition-by identity (fakprima %)) varg)
                (apply concat)
                (group-by first)
                (filter #(= (count (val %)) cv))
                (map (fn [x] [(key x) (apply min (map count (val x)))]))
                (concat [(repeat cv 1)])
                (map #(pangkat (first %) (second %)))
                (reduce *))))

(defn kpk1
  "Smallest common multiplier of numbers"
  [x y & more]
  (let [ varg (conj more x y)]
           (->> (map #(partition-by identity (fakprima %)) varg)
                (apply concat)
                (group-by first)
                (map (fn [x] [(key x) (reduce max (map count (val x)))]))
                (map #(pangkat (first %) (second %)))
                (reduce *))))

(defn perbandingan
  "Change ratio to a : b form"
  [x]
  (if (ratio? x) (str (numerator x) " : " (denominator x)) (str x " : " 1)))

(defn mutlak
  "Absolute value of a number"
  [x]
  (if (< x 0) (* -1 x) x))

(defn tanda
  "Give +, - latex sign"
  [x]
  (cond (> x 0) "$+$" (< x 0) "$-$" :else ""))

(defn titik2koma
  "Change titik to koma"
  [x]
  (cs/replace x #"\." ","))
