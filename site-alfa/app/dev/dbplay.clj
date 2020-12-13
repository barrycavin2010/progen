(ns dbplay
  (:require
    [app.utils :as util]
    [taoensso.carmine :as car :refer [wcar]]
    ;;    [rethinkdb.query :as r]
    ))

(comment (defonce conn (atom (r/connect :host "127.0.0.1" :port 28015 :db "ZenCore"))))

(defonce conn {:pool {} :spec {}})



(def more-soals
  (for [level (range 1000)
        text ["Jumpalitans" "Fooo" "Fuckshit"]
        kode ["one" "two"]]
    {:level level
     :text text
     :kode kode
     :options [{:text "this is" :value true}
               {:text "that is" :value false}]}))

(def soals
  [{:kode "fuck"
    :text "this is the text"
    :options [{:text "this is option text" :value true}
              {:text "this is another option" :value false}]}
   {:kode "fuck1"
    :text "this is the text nomero 2"
    :options [{:text "this is option text" :value true}
              {:text "this is another option" :value false}]}])

(comment
  (defn dbmap
    [table-name]
    {:konn @conn :dbname "ZenCore" :table-name table-name})

  (defn create-db
    [konn]
    (-> (r/db-create "ZenCore")
        (r/run konn)))

  (defn create-table
    [konn table-name]
    (-> (r/table-create table-name)
        (r/run konn)))

  (defn insert-data
    [{:keys [konn dbname table-name]} data]
    (-> (r/db dbname)
        (r/table table-name)
        (r/insert data)
        (r/run konn))))






