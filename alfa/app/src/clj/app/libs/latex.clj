(ns alfa.libs.latex
  (:require [clojure.string :as cs]))

(defn exp
  [x y]
  (reduce * (repeat y x)))

(defn float->ratio
  "Convert float to ratio
   (float->ratio 1.5) => 3/2
   (float->ratio -1.5) => -3/2"
  [fl]
  (let [v (partition-by #(= % \.) (seq (str fl)))
        n (count (last v))
        denom (exp 10 n)
        nume (int (* fl denom))]
    (/ nume denom)))

(defn eratio
  "Convert number of type integer. ratio, or float to an enrich form a b/c
  (eratio 2) => 2
  (eratio 7/2) => 3 1/2
  (eratio 1.25) => 1 1/4
  Latex format :
  (eratio 2) => $2$
  (eratio 7/2) $3\\frac{1}{2}$ ==> 3 1/2
  (eratio 1.25) $1\\frac{1}{4}$ ==> 1 1/4"
  [everything]
  (let [c (cond
            (or (integer? everything) (ratio? everything)) everything
            :else (float->ratio everything))]
    (cond
      (integer? c) (str "$" c "$")
      (< 0 c 1) (str "$\\frac{" (numerator c) "}{" (denominator c) "}$")
      (< -1 c 0)
      (let [d (* -1 c)] (str "$-\\frac{" (numerator d) "}{" (denominator d) "}$"))
      (> c 1) (let [a (numerator c)
                    b (denominator c)
                    hb (quot a b)
                    sf (/ (rem a b) b)]
                (str "$" hb "\\frac{" (numerator sf) "}{" (denominator sf) "}$"))
      :else (let [d (* -1 c)
                  a (numerator d)
                  b (denominator d)
                  hb (quot a b)
                  sf (/ (rem a b) b)]
              (str "$-" hb "\\frac{" (numerator sf) "}{" (denominator sf) "}$")))))

(defn decform
  "Convert any number to a form xx,yy (Basically round to dk number after coma).
  (decform 4.123 2) ==> 4,12
  (decform 7/2) => 3,50
  (decform 125 3) => 125,000"
  [number dk]
  (-> (format (str "%." dk "f") (* number 1.0))
      (cs/replace #"\." ",")))

(defn tribuan
  "Tanda titik untuk Ribuan
  (tribuan 1200) => 1.200
  (tribuan 12345) => 12.345
  (tribuan 1234567) => 1.234.567"
  [x]
  (let [xs (seq (if (integer? x) (str x) x))
        fc (not-any? #{\,} xs)
        pre (if (= \- (nth xs 0)) "-" "")
        post (if fc "" (apply str (drop (.indexOf xs \,) xs)))
        mid1 (if (= pre "-") (rest xs) xs)
        mid2 (if (not= post "") (take (.indexOf mid1 \,) mid1) mid1)]
    (str pre
        (clojure.string/reverse
          (apply str
            (interpose "."
              (map #(apply str %)
                (partition-all 3 (reverse mid2))))))
        post)))

(defn greek
  "Greek Letters
  (greek :alpha) ==> $\\alpha$
  List of greek Letters:
  :alpha :beta :gamma :delta :epsilon :zeta :eta :theta :iota :kappa :lambda
  :mu :nu :xi :pi :rho :sigma :tau :upsilon :phi :chi :psi :omega
  Uppercase greek letters are obtained by making the first character of the name upper case."
  [nkey]
  (str "$\\" (name nkey) "$"))

(defn sqrt
  "Square root
  (sqrt 12) ==> $\\sqrt{12}$
  (sqrt 3 12) ==> $\\sqrt[3]{12}$"
  ([x] (str "$\\sqrt{" x "}$"))
  ([x y] (str "$\\sqrt[" x "]{" y "}$")))

(defn expres
  "Create Expression
  (expres [2 'x^2'][3 'x'][-5]['='][0]) ==> '2x^2+3x-5=0'
  (expres [1 'x^2'][0 'x'][-5]['='][0]) ==> 'x^2-5=0'
  (expres [2 'C_2H_6'][3 'H_2O']['\\to '][4 'CO_2'][6 'O_2']) ==> '2C_2H_6+3H_2O\\to 4CO_2+6O_2'"
  [vek & more]
  (let [av (vec (cons vek more))
        cs (fn [vs] (apply str vs))
        ang? (fn [x] (or (integer? x) (ratio? x) (float? x)))]
    (loop [lat "" sg true i 0]
      (if (= i (count av))
        lat
        (let [c (count (av i))
              favi (first (av i))
              nstr (if (> c 1)
                     (cond
                       (== favi 1) (cs (rest (av i)))
                       (== favi -1) (str "-" (cs (rest (av i))))
                       (== favi 0) ""
                       :else (cs (av i)))
                     (cs (av i)))]
          (recur
            (cond
              sg (str lat nstr)
              (ang? favi) (if (> favi 0) (str lat "+" nstr) (if (= favi 0) lat (str lat nstr)))
              :else (str lat nstr))
            (if (ang? favi)
              (if (and (> c 1) (= favi 0)) sg false)
              true)
            (inc i)))))))

(defn sratio [x]
  "Change number to 'a : b' form
  (sratio 4) ==> '4 : 1'
  (sratio 15/10) ==> '3 : 2'
  (sratio 1.5) ==> '3 : 2'"
  (cond
    (integer? x) (str x " : 1")
    (ratio? x) (str (numerator x) " : " (denominator x))
    :else (let [y (float->ratio x)] (str (numerator y) " : " (denominator y)))))

(defn akar
"Simplify root form
(akar 4) ==> 2
(akar 8) ==> $2\\sqrt{2}$"
[x]
  (let [a (filter #(= (rem x %) 0) (range 2 (inc (Math/round (Math/ceil (/ x 2))))))
        b (filter #(= (Math/sqrt %) (Math/floor (Math/sqrt %))) a)]
      (cond
        (= (Math/sqrt x) (Math/floor (Math/sqrt x))) (Math/round (Math/sqrt x))
        (= b []) (str "$\\sqrt{" x "}$")
        :else (str "$" (Math/round (Math/sqrt (apply max b)))
              "\\sqrt{" (/ x (apply max b)) "}$"))))

