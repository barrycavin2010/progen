(ns alfa.problem.old-problem
  (:require [alfa.utils :refer :all]))

;; These are addhoc functions to convert old format data into acceptable data format
;; keep it here just in case we need it some time in the future

(comment
  (defn load-problems
    []
    (cslurp "resources/old-problem/soal.edn"))

  (defn get-problem
    []
    (->> (load-problems)
         (group-by :kode)
         (map val)
         rand-nth))

  (defn loading
    [which]
    (cslurp (str "resources/old-problem/" which ".edn")))

  (defn convert-uuid
    []
    (let [data (cslurp "resources/old-problem/uuid.edn")]
      (->> (keys data)
           (zipmap (map :squuid (vals data)))
           (cspit "resources/old-problem/squuid-kode.edn"))))

  (defn convert
    []
    (let [raw-data (loading "squuid-kode")
          kode->uuid (zipmap (vals raw-data) (keys raw-data))
          soals (->> (loading "soal")
                     (mapv #(assoc % :id (kode->uuid (:kode %)))))
          new-soals (group-by :id soals)]
      (cspit "resources/old-problem/new-soal.edn" new-soals))))

