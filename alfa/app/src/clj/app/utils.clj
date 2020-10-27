(ns alfa.utils
  (:require
    [clj-time.core :as t]
    [clj-time.format :as f]
    [clj-uuid :as uuid]
    [clojure.edn :as edn]
    [taoensso.timbre :as log]
    [cheshire.core :refer [parse-string]]
    [clojure.string :as cs]
    [clojure.pprint :refer [pprint]]
    [clojure.java.io :as io]
    [me.raynes.fs :as fs]))

(defonce error-counter (atom 0))
(defonce perpetrators (atom []))

(defn info [& body]
  (apply println "INFO :" body))

(defn error [& body]
  (swap! error-counter inc)
  (apply println "ERROR :" body))

(defn warn [& body]
  (apply println "WARNING :" body))

(defn conpath
  "Content directory path"
  [dir]
  (->> (cs/split (str dir) #"/")
       (drop 6)
       (interpose "/")
       (cons "/")
       (apply str)))

#_(defn parent-dir
    "Content directory path"
    [dir]
    (->> (cs/split (str dir) #"/")
         (drop-last 2)
         rest
         (interpose "/")
         (apply str)))

(def pres clojure.pprint/pprint)

(defmacro pro-catch
  "Macro to report problem error"
  [message coder exprs]
  `(try ~exprs
        (catch Exception ~(gensym)
          (error ~message ~coder)
          (throw (Exception. ~message)))))

(defmacro no-throw
  "Macro to report error without exception"
  [message some-var exprs]
  `(try ~exprs
        (catch Exception ~(gensym)
          (error ~message ~some-var))))

(defmacro silent-try
  "Macro to report error without exception"
  [exprs]
  `(try ~exprs
        (catch Exception ~(gensym))))

(defn pro-rep
  "Reporting error"
  [message coder]
  (error message coder)
  (throw (Exception. message)))

(defn get-os
  []
  (let [res (System/getProperty "os.name")]
    (if (cs/includes? res "Win") :win :posix)))

(defn create-path
  "Given one argument it returns the dir path from a vector of dir path."
  ([vec-path os]
   (if (= :win os)
     (->> vec-path
          (interpose "\\")
          (cons "\\")
          (apply str))
     (->> vec-path
          (interpose "/")
          (cons "/")
          (apply str))))
  ([vec-path]
   (create-path vec-path (get-os))))

(defn path
  "Create a string path of string dir to string child"
  [dir child]
  (create-path (concat (fs/split dir) [child])))

(defn main-path
  "Function to return the main path of the zenpres-school"
  ([vec?] (->> (fs/split fs/*cwd*)
               rest
               (drop-last 2)))
  ([] (->> (fs/split fs/*cwd*)
           rest
           (drop-last 2)
           create-path)))


(defn cslurp
  "Helper function to easily slurp and read-string a file"
  [fname]
  ((comp edn/read-string slurp) fname))

(defn cspit
  "Helper function to beautifully print clojure to file"
  [fname data]
  (->> data pprint with-out-str (spit fname)))

(defn collect-error
  [coder]
  (swap! perpetrators conj coder)
  coder)

(defn reset-collector
  []
  (reset! perpetrators []))

#_(defn now
    "Returns the current date and time"
    []
    (f/unparse (f/formatter "yyyyMMdd-hhmmss") (t/now)))

#_(defn now-date
    "Given zero argument, it returns YYYYMMDD, given one argument it returns YYYY-MM-DD"
    ([]
     (f/unparse (f/formatter "yyyyMMdd") (t/now)))
    ([_]
     (f/unparse (f/formatter "yyyy-MM-dd") (t/now))))

(defn uuid
  "When given zero argument, it returns a uuid/v1, given one arguments, it returns
  a list of n uuids."
  ([]
   (cs/replace (str (uuid/v1)) #"-" ""))
  ([n]
   (repeatedly n uuid)))

#_(defn to-uuid
    "Give instance to string-uuid.
    example :
    (to-uuid \"f49481b0-6368-11e5-9408-06f7c2205087\")
    =>  #uuid \"f49481b0-6368-11e5-9408-06f7c2205087\""
    [str-uuid]
    (uuid/as-uuid str-uuid))



