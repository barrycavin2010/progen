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

(defn grammar-02
  []
  (->> (for [tense [:simple :cont]
             verb [:watch :teach :play :have]
             subject ["He" "I" "She"]
             clue (vec (range 4))]
         (let [[p1 p2 p3] (condp = tense
                            :simple (cond (#{"He" "She"} subject)
                                          ({:watch ["watch movie" "is watching movie" "watchs movie"]
                                            :teach ["teach the class" "teachs the class" "is teaching the class"]
                                            :play  ["play badminton" "is playing badminton" "playing badminton"]
                                            :have  ["have a standup meeting" "is having a standup meeting" "having a standup meeting"]}
                                           verb)
                                          :others
                                          ({:watch ["watches movie" "am watching movie" "watching movie"]
                                            :teach ["teaches the class" "teachs the class" "am teaching the class"]
                                            :play  ["plays badminton" "am playing badminton" "playing badminton"]
                                            :have  ["has a standup meeting" "am having a standup meeting" "having a standup meeting"]}
                                           verb))
                            :cont (cond (#{"He" "She"} subject)
                                        ({:watch ["watches movie" "watching movie" "are watching movie"]
                                          :teach ["teaches the class" "are teaching the class" "teaching the class"]
                                          :play  ["plays badminton" "are playing badminton" "playing badminton"]
                                          :have  ["has a standup meeting" "are having a standup meeting" "having a standup meeting"]}
                                         verb)
                                        :others
                                        ({:watch ["watch movie" "is watching movie" "watching movie"]
                                          :teach ["teaches the class" "teach the class" "is teaching the class"]
                                          :play  ["play badminton" "is playing badminton" "playing badminton"]
                                          :have  ["have a standup meeting" "is having a standup meeting" "having a standup meeting"]}
                                         verb)))
               sisa {:tense          ({:simple "simple present tense"
                                       :cont   "present continous tense"} tense)
                     :formula        ({:simple "S + V1"
                                       :cont   "S + am/is/are + Ving"} tense)
                     :bentuk-subject (if (= "I" subject)
                                       "ganti pertama tunggal"
                                       "ganti ketiga tunggal")}]
           (merge sisa
                  (condp = tense
                    :simple (cond
                              (#{"He" "She"} subject)
                              {:subject subject
                               :clue    (["every Friday" "at 11 am everyday" "every weekend" "every morning"] clue)
                               :pb      ({:watch "watches movie"
                                          :teach "teaches the class"
                                          :play  "plays badminton"
                                          :have  "has a standup meeting"} verb)
                               :p1      p1 :p2 p2 :p3 p3}
                              :others
                              {:subject subject
                               :clue    (["every Friday" "at 11 am everyday" "every weekend" "every morning"] clue)
                               :pb      ({:watch "watch movie"
                                          :teach "teach the class"
                                          :play  "play badminton"
                                          :have  "have a standup meeting"} verb)
                               :p1      p1 :p2 p2 :p3 p3})
                    :cont (cond
                            (#{"He" "She"} subject)
                            {:subject subject
                             :clue    (["now" "at the moment" "at this hour" "right now"] clue)
                             :pb      ({:watch "is watching movie"
                                        :teach "is teaching the class"
                                        :play  "is playing badminton"
                                        :have  "is having a standup meeting"} verb)
                             :p1      p1 :p2 p2 :p3 p3}
                            :others
                            {:subject subject
                             :clue    (["now" "at the moment" "at this hour" "right now"] clue)
                             :pb      ({:watch "am watching movie"
                                        :teach "am teaching the class"
                                        :play  "am playing badminton"
                                        :have  "am having a standup meeting"} verb)
                             :p1      p1 :p2 p2 :p3 p3})))))
       shuffle))



