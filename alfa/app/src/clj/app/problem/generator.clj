(ns alfa.problem.generator
  (:require
    [clojure.java.io :as io]
    [alfa.utils :refer :all]
    [me.raynes.fs :as fs]
    [taoensso.timbre :as log]
    [clojure.string :as cs]
    [selmer.parser :refer [render]]))

(declare single multiple blank blank-string causal simple
         set-single set-blank-string set-multiple set-blank)

(defn generate
  "Callout function for soal processing"
  [gen-fn {:keys [set? type test generator coder] :as problem-map}]
  (let [mapset {:single       set-single
                :multiple     set-multiple
                :blank        set-blank
                :blank-string set-blank-string}
        mapi {:single       single
              :multiple     multiple
              :causal       causal
              :blank        blank
              :blank-string blank-string}]
    (if generator
      (let [progen (try (gen-fn generator)
                        (catch Exception e
                          (error "Generator error caused by" (.getMessage e))
                          (collect-error coder)
                          (throw (Exception. "Generator error"))))]
        (when-let [res (try (let [popo (future (last (for [i (range 50)] (progen))))]
                              (loop [i 0]
                                (if (> i 200)
                                  (do (future-cancel popo)
                                      (throw (Exception. "lack of awesomeness factor, and it also causes the hell to freeze for a while")))
                                  (if (future-done? popo)
                                    @popo
                                    (do (Thread/sleep 3)
                                        (recur (inc i)))))))
                            (catch Exception e
                              (error "Generator error caused by" (.getMessage e))
                              (collect-error coder)
                              (throw (Exception. "Generator error"))))]
          (when-not (map? res) (throw (Exception. "The result of the generator must be a clojure map"))))
        (if test
          (if (try ((gen-fn test) progen)
                   (catch Exception e
                     (error "Test fn error caused by" (.getMessage e))))
            (if set?
              ((mapset type) gen-fn problem-map)
              ((mapi type) gen-fn problem-map))
            (do (collect-error coder)
                (pro-rep "The test failed... by" coder)))
          (if set?
            ((mapset type) gen-fn problem-map)
            ((mapi type) gen-fn problem-map))))
      (if set?
        ((mapset type) gen-fn problem-map)
        ((mapi type) gen-fn problem-map)))))

(def choice-map
  "Tranform position into A/B/C/D.."
  (zipmap (range) (map str "ABCDEFGHIJKLMOPQRSTUVWYZ")))

(def causal-relation
  {[true true true]  "A"
   [true true false] "B"
   [true false]      "C"
   [false true]      "D"
   [false false]     "E"})

(defn f-causal
  "Map builder for causal problems"
  [relation-map]
  (let [{:keys [status relations]} relation-map
        choices (range (count status))
        ch1 (rand-nth choices)
        ch2 (->> choices (remove #{ch1}) rand-nth)
        ans (mapv status [ch1 ch2])]
    {:sebab  ch1
     :akibat ch2
     :answer (causal-relation
               (if (every? true? ans)
                 (conj ans (boolean ((relations ch1) ch2)))
                 ans))}))

(defn causal
  "Processor for causal problems"
  [gen-fn {:keys [soal generator relations pembahasan coder script id] :as problem-map}]
  (let [rel-map (read-string relations)
        gen-result (if (boolean generator)
                     ((gen-fn generator))
                     {})
        papa (cs/split (render soal gen-result) #"==sepa==")
        header (first papa)
        soals (vec (rest papa))
        {:keys [sebab akibat answer]} (f-causal rel-map)]
    (try {:header     header
          :pembahasan pembahasan
          :sebab      (soals sebab)
          :akibat     (soals akibat)
          :type       :causal
          :set?       false
          :answer     answer
          :script     script
          :id         id}
         (catch Exception e
           (error "Generator or relations error in causal problem by" coder)
           (error "Generator or relations error caused by" (.getMessage e))))))

(defn blank
  "Process soal blank"
  [gen-fn {:keys [soal id script generator answers coder pembahasan] :as problem-map}]
  (let [inst-map (pro-catch "Blank problem generator error by" coder
                            ((gen-fn generator)))
        papa (cs/split (render soal inst-map) #"==sepa==")
        header (first papa)
        soals (vec (rest papa))
        ans (pro-catch "Blank problem answer-fn error by" coder
                       (vec ((gen-fn answers) inst-map)))
        ctr (count soals)
        nsoal (rand-int ctr)
        soal-presentation (soals nsoal)]
    (if (and (vector? ans)
             (every? string? ans)
             (== (count soals) (count ans)))
      {:header     header
       :pembahasan pembahasan
       :soal       soal-presentation
       :type       :blank
       :answer     (ans nsoal)
       :set?       false
       :id         id
       :script     script}
      (pro-rep "Soal & math dont match, blank problem error presented by" coder))))

(defn blank-string
  "Process soal blank-string"
  [gen-fn {:keys [soal id script answers pembahasan coder] :as problem-map}]
  (let [papa (cs/split soal #"==sepa==")
        header (first papa)
        soals (vec (rest papa))
        ans (pro-catch "answer-fn error for blank-string problem by"
                       coder
                       (read-string answers))
        ctr (count soals)
        nsoal (rand-int ctr)
        soal-presentation (soals nsoal)]
    (if (and (vector? ans)
             (every? vector? ans)
             (every? #(every? string? %) ans)
             (== (count soals) (count ans)))
      {:header     header
       :pembahasan pembahasan
       :soal       soal-presentation
       :type       :blank-string
       :answers    (ans nsoal)
       :set?       false
       :id id
       :script     script}
      (pro-rep "Blank string problem error, dont meet the standard, by" coder))))

(defn set-blank-string
  "Process soal blank-string"
  [gen-fn {:keys [soal id script answers pembahasan coder] :as problem-map}]
  (let [papa (cs/split soal #"==sepa==")
        header (second papa)
        soals (vec (nnext papa))
        ans (pro-catch "Set blank string problem error by"
                       coder
                       (read-string answers))]
    (if (and (vector? ans)
             (every? vector? ans)
             (every? #(every? string? %) ans)
             (== (count soals) (count ans)))
      {:header     header
       :pembahasan pembahasan
       :soals      soals
       :answers    ans
       :type       :blank-string
       :set?       true
       :id id
       :script     script}
      (pro-rep "Set blank string problem error by" coder))))

(defn set-blank
  "Process soal blank"
  [gen-fn {:keys [soal id script generator answers pembahasan coder] :as problem-map}]
  (let [inst-map (pro-catch "Set Blank  problem error by"
                            coder
                            (read-string answers))
        papa (-> (render soal inst-map)
                 (cs/split #"==sepa=="))
        header (second papa)
        soals (vec (nnext papa))
        ans (pro-catch "Evaluating answer-fn of set-blank problem by "
                       coder
                       ((gen-fn answers) inst-map))]
    (if (and (vector? ans)
             (every? string? ans)
             (== (count soals) (count ans)))
      {:header     header
       :pembahasan pembahasan
       :soals      soals
       :answers    ans
       :type       :blank
       :set?       true
       :id id
       :script     script}
      (pro-rep "Set blank problem error by" coder))))

(defn f-selection
  "Soal processor for selection types problem"
  [soal-without-header]
  (let [[a b] (cs/split soal-without-header #"==choices==")]
    {:text    a
     :choices (->> (cs/split-lines b)
                   (remove empty?)
                   (mapv #(cs/split % #"::"))
                   (mapv #(update-in % [0] read-string))
                   shuffle)}))

(defn single
  "Process soal string and break them apart into clojure's friendly format"
  [gen-fn {:keys [soal generator id script coder pembahasan] :as problem-map}]
  (let [papa (pro-catch "Generator error in single problem by" coder
                        (-> soal
                            (render (if (boolean generator)
                                      ((gen-fn generator))
                                      {}))
                            (cs/split #"==sepa==")))
        header (first papa)
        soals (vec (rest papa))
        ctr (count soals)
        soal (rand-int ctr)
        soal-presentation (pro-catch "soal.html error in single problem by" coder
                                     (f-selection (soals soal)))
        answer (pro-catch "in the choices of the single problem by" coder
                          (->> (:choices soal-presentation)
                               (map-indexed #(do [%1 (first %2)]))
                               (filterv second)
                               ffirst
                               choice-map))]
    (pro-catch "soal.html error in single problem by" coder
               (mapv #(f-selection %) soals))
    {:header     header
     :pembahasan pembahasan
     :soal       soal-presentation
     :answer     answer
     :type       :single
     :set?       false
     :id id
     :script     script}))

(defn multiple
  "Process soal string and break them apart into clojure's friendly format for multiple"
  [gen-fn {:keys [soal id script generator coder pembahasan] :as problem-map}]
  (let [papa (pro-catch "Generator error in multiple problem by" coder
                        (-> soal
                            (render (if (boolean generator)
                                      ((gen-fn generator))
                                      {}))
                            (cs/split #"==sepa==")))
        header (first papa)
        soals (vec (rest papa))
        ctr (count soals)
        soal (rand-int ctr)
        soal-presentation (pro-catch "soal.html error in multiple problem by" coder
                                     (f-selection (soals soal)))
        answer (pro-catch "Choices error in multiple problem by" coder
                          (->> (:choices soal-presentation)
                               (map-indexed #(do [%1 (first %2)]))
                               (filterv second)
                               (mapv first)
                               (mapv choice-map)))]
    (pro-catch "soal.html error in multiple problem by" coder
               (mapv #(f-selection %) soals))
    {:header     header
     :pembahasan pembahasan
     :soal       soal-presentation
     :answers answer
     :type :multiple
     :id id
     :script     script
     :set? false}))

(defn set-single
  "Process soal string and break them apart into clojure's friendly format"
  [gen-fn {:keys [soal id script generator pembahasan coder] :as data}]
  (let [papa (pro-catch "Generator error in set-single problem by" coder
                        (-> soal
                            (render (if (boolean generator)
                                      ((gen-fn generator))
                                      {}))
                            (cs/split #"==sepa==")))
        header (second papa)
        soals (vec (nnext papa))
        soals-presentation (pro-catch "soal.html error in set-single problem by" coder
                                      (mapv f-selection soals))
        answer (pro-catch "Choices error in set-single problem by" coder
                          (->> (map :choices soals-presentation)
                               (map (fn [x] (map-indexed #(do [%1 (first %2)]) x)))
                               (mapv (fn [x] (ffirst (filter second x))))
                               (mapv choice-map)))]
    (when (cs/includes? header "==choices==")
      (pro-rep "For set, the first sepa should not be a problem, but a text or info" coder))
    {:header     header
     :pembahasan pembahasan
     :soals      soals-presentation
     :answers    answer
     :type       :single
     :id id
     :script     script
     :set?       true}))

(defn set-multiple
  "Process soal string and break them apart into clojure's friendly format for multiple"
  [gen-fn {:keys [soal id script generator pembahasan coder] :as data}]
  (let [papa (pro-catch "Generator error in set-multiple problem by" coder
                        (-> soal
                            (render (if (boolean generator)
                                      ((gen-fn generator))
                                      {}))
                            (cs/split #"==sepa==")))
        header (second papa)
        soals (vec (nnext papa))
        soals-presentation (pro-catch "Choices error in set-multiple problem by" coder
                                      (mapv f-selection soals))
        answer (pro-catch "Choices error in set-multiple problem by" coder
                          (->> (map :choices soals-presentation)
                               (map (fn [x] (map-indexed #(do [%1 (first %2)]) x)))
                               (mapv (fn [x] (filterv second x)))
                               (mapv #(mapv choice-map (mapv first %)))))]
    (when (cs/includes? header "==choices==")
      (pro-rep "For set, the first sepa should not be a problem, but a text or info" coder))
    {:header     header
     :pembahasan pembahasan
     :soals      soals-presentation
     :answers    answer
     :id id
     :script     script
     :type       :multiple
     :set?       true}))
