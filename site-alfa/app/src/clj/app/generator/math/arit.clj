(ns app.generator.math.arit
  (:require [clojure.set :as cset]))

(defn rand+
  [x]
  (let [choices (->> (range (- x 3) (+ x 3))
                     (remove #(= x %))
                     shuffle
                     (take 4))]
    (zipmap [:p1 :p2 :p3 :p4] choices)))

(defn arit-01
  []
  (->> (for [b (range 30 50)
             a (range (+ b 2) 50)
             blank [:a :b :c]]
         (let [ab a bb b cb (+ a b)
               mapi {:ab ab :bb bb :cb cb :a a :b b :c cb}]
           (condp = blank
             :a (merge mapi {:a "___" :pb a} (rand+ a))
             :b (merge mapi {:b "___" :pb b} (rand+ b))
             :c (merge mapi {:c "___" :pb cb} (rand+ cb)))))
       (shuffle)
       (take 10)))
