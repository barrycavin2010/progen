(ns app.generator.english.grammar
  (:require [clojure.set :as cset]))

(defn male-subjects
  []
  ["Steven" "Calvin" "Firmino" "Roberto" "Ronaldo"])

(defn pronouns
  []
  {:male         ["he" "him" "his" "his"]
   :female       ["she" "her" "her" "his"]
   :second       ["you" "you" "your" "yours"]
   :first-single ["I" "me" "my" "mine"]
   :first-plural ["we" "us" "our" "ours"]
   :third-plural ["they" "them" "their" "theirs"]
   :thing        ["it" "it" "its" "its"]})

(defn grammar-01
  []
  (let [k1s ["the morning flight" "the morning train" "the early bus"]
        k2s ["the wedding" "the meeting" "the presentation" "the movie's premier" "the football game"]
        s1s (male-subjects)
        pros ["pertama" "ketiga"]]
    (->> (for [k1 k1s k2 k2s s1 s1s pro pros]
           (if (= "pertama" pro)
             {:k1 k1 :k2 k2 :pro pro :s1 s1 :s2 "I"
              :pb "us" :p1 "him" :p2 "them" :p3 "me"}
             {:k1 k1 :k2 k2 :pro pro :s1 s1 :s2 (rand-nth (vec (cset/difference (set s1s) #{s1})))
              :pb "them" :p1 "him" :p2 "us" :p3 "he"}))
         shuffle
         (take 30))))



