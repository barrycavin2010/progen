(ns alfa.grabber.reporting
  (:require [dk.ative.docjure.spreadsheet :as xl]
            [alfa.utils :refer :all]))

(defn- container-builder
  [refs con-id cg-level]
  (let [con-data (@(:container refs) con-id)
        f (fn [x] (silent-try (@(get-in refs [(:type x)]) (:id x))))
        pl-content (loop [[pl & pls] (:playlist con-data) res []]
                     (if pl
                       (recur
                         pls
                         (conj res ["  "
                                    (->> (concat (repeat cg-level "   ") (str (name (:type pl))))
                                         (apply str))
                                    (if (f pl) " exists " " error ")
                                    "      "
                                    "                            "]))
                       res))]
    (->> (cons ["  "
                (->> (str "Container : " (:title con-data))
                     (concat (repeat (dec cg-level) "   "))
                     (apply str))
                " exists "
                "      "
                "                            "]
               pl-content)
         vec)))

(defn cg-builder
  [refs cg-id]
  (let [cg-data (@(:content-group refs) cg-id)
        cg-children (:cg-children cg-data)
        containers (->> (:cg-containers cg-data)
                        (sort-by :sequence))
        con-data (mapcat #(container-builder refs (:id %) (:level cg-data)) containers)
        cg-report ["  "
                   (->> (str "Content group : " (:title cg-data))
                        (concat (repeat (- (:level cg-data) 2) "   "))
                        (apply str))
                   " exists "
                   " "
                   " "
                   "     "
                   "                            "]]
    (if (empty? cg-children)
      (cons cg-report con-data)
      (->> (mapcat #(cg-builder refs (:id %)) cg-children)
           (concat [cg-report] con-data)
           vec))))

(defn generate-report
  "Generate an xl report for level-specific content mode.
  Content modes are :sda, :smp, or :sma (specified by system in which it started)
  The reports generated will be from level 2 i.e. Matematika Kelas 12 Kur13.
  And thus there will be several reports generated at once."
  [target-dir refs]
  (let [level-2 (->> (vals @(:content-group refs))
                     (filter #(= 3 (:level %))))]
    (info "=====================Producing QC xls reports===================")
    (info "===============There are" (count level-2) "reports generated===================")
    (loop [[cg & cgs] level-2]
      (when cg
        (print "-")
        (let [parent-name (->> (:cg-parents cg)
                               (filter #(= 1 (:distance %)))
                               first :title)]
          (->> (xl/create-workbook "Zenpres QC2 report"
                                   (->> (cg-builder refs (:id cg))
                                        (cons ["No" "Content Name" "System check" "Jumlah Soal" "QC1" "QC2" "Comment"])
                                        vec))
               (xl/save-workbook! (path target-dir (str parent-name " " (:title cg) ".xlsx")))))
        (recur cgs)))
    (println "")))

