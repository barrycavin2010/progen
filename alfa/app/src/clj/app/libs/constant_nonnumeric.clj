(ns alfa.libs.constant-nonnumeric)

(def nama
  {:women ["Ana" "Aisyah" "Alyssa" "Brown" "Dinah" "Denise" "Dina" "Dea" "Dona" "Ellie" "Fani" "Flo" "Hani" "Jean" "Joanna" "Joan" "Kimberly" "Laura" "Lauren" "Mira" "Potter" "Rachel" "Sarah" "Sherley" "Tita" "Vina"]
   :men   ["Adji" "Arie" "Arifian" "Bejo" "Charlie" "Chuck" "Davin" "Dono" "Draco" "Dimas" "Darto" "Ijul" "Jon" "Joni" "Potter" "Peter" "Stanley" "Sudiro" "Tim" "Tom" "Wisnu" "Zulfikar"]})

(def colour ["red" "black" "white" "blue" "yellow" "green" "purple" "brown" "pink"])

(def things
  {:animal     ["cat" "dog" "pet"]
   :stationary ["bag" "book" "eraser" "paper" "pencil" "pen" "sharpener" "ruler" "ink"]

   :big        ["car" "house"]
   :small      ["CD" "umbrella"]

   :drink      ["juice" "milk" "syrup" "tea" "water" "coffee"]
   :food       ["chicken pie" "egg" "pie" "rice"]
   :fruit      ["apple" "melon" "orange" "pineapple" "watermelon"]
   :spice      ["salt" "sugar" "honey" "jam"]

   :game       ["american football" "badminton" "basketball" "chess" "cricket" "dodgeball" "football" "rugby" "table tennis" "tennis" "volleyball"]
   :music      ["bass" "cello" "drum" "guitar" "piano" "violin"]
   :study      ["Anthropology" "Chemical Engineering" "History" "Mathematics" "Mechanical Engineering" "Science"]

   :place      ["school" "your house" "the supermarket" "the station"]})

(def time-lib
  {:day   ["Sunday" "Monday" "Tuesday" "Wednesday" "Thursday" "Friday" "Saturday"]
   :point ["week" "month" "year"]
   :time  ["morning" "afternoon" "evening" "night"]})

(def past-lib
  (concat
    ["yesterday" "this morning" "this afternoon" "this evening"]
    (map str
         (repeat "last")
         (repeat " ")
         (:day time-lib))
    (map str
         (repeat "last")
         (repeat " ")
         (:point time-lib))
    (map str
         (repeat "yesterday")
         (repeat " ")
         (:time time-lib))))

(def future-lib
  (concat
    ["tomorrow" "this afternoon" "this evening"]
    (map str
         (repeat "next")
         (repeat " ")
         (:day time-lib))
    (map str
         (repeat "next")
         (repeat " ")
         (:point time-lib))
    (map str
         (repeat "tomorrow")
         (repeat " ")
         (:time time-lib))))

(def routine-lib
  (concat
    (map str
         (repeat "every")
         (repeat " ")
         (:day time-lib))
    (map str
         (repeat "every")
         (repeat " ")
         (:point time-lib))
    (map str
         (repeat "every")
         (repeat " ")
         (:time time-lib))))
