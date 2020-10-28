(ns app.generator.tenses)

(def verbs
  [{:v1s "sleeps"
    :v1p "sleep"
    :v2 "slept"
    :v3 "slept"
    :ving "sleeping"}
   {:v1s "reads"
    :v1p "read"
    :v2 "read"
    :v3 "read"
    :ving "reading"}])

(def tenses-map
  {:p1simple "Simple present tense"
   :p1cont "Present continous tense"
   :p2simple "Simple past tense"
   :p2cont "Past continous tense"})

(def tenses-clues
  {:p1simple ["every day" "every weekend" "every night"]
   :p1cont ["now" "right now" "at this hour"]
   :p2simple ["yesterday" "two days ago" "this morning"]
   :p2cont ["while she came" "when the lighting struck"]})

(def subject-map
  {:single-1 "Orang pertama tunggal"
   :plural-1 "Orang pertama jamak"
   :single-2 "Orang kedua tunggal"
   :plural-2 "Orang kedua jamak"
   :single-3 "Orang ketiga tunggal"
   :plural-3 "Orang ketiga jamak"})

(def subject-verb
  {:single-1 " am "
   :plural-1 " are "
   :single-2 " are "
   :plural-2 " are "
   :single-3 " is "
   :plural-3 " are "})

(def subject-examples
  {:single-1 ["I"]
   :plural-1 ["We"]
   :single-2 ["You"]
   :plural-2 ["You" "You guys"]
   :single-3 ["He" "She" "Andy" "Montok"]
   :plural-3 ["They" "The boys" "The girls" "The customers"]})





