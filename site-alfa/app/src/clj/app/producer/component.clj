(ns app.producer.component
  (:require
    [com.stuartsierra.component :as component]
    [app.producer.register :as regis]
    [app.producer.producer :as producer]
    [clojure.edn :as edn]
    [clojure.string :as cs]
    [app.utils :refer :all]))

(declare grab-produce store grab)

(defrecord Producer [source target]
  component/Lifecycle
  (start [this]
    (let [all-data (regis/soal-map)
          content (grab-produce source target all-data)]
      (merge this content)))
  (stop [this]
    (dissoc this :templates :problem-map)))

(defn make [content-config]
  (map->Producer content-config))

(defn- grab-produce
  "Grabbing problems based on source & registered problems"
  [source target all-data]
  (let [problem-metas (mapv #(grab source %) all-data)
        generated-problems (mapv producer/generate problem-metas)
        templates (->> (mapv #(dissoc % :gen-fn :soal :bahasan) problem-metas)
                       (mapv #(assoc % :template-id (get-in % [:meta :template-id]))))
        results (mapv #(merge %1 {:problems %2})
                      (mapv #(dissoc % :gen-fn :soal :bahasan) problem-metas)
                      generated-problems)
        problem-map (-> #(get-in % [:meta :template-id])
                        (map problem-metas)
                        (zipmap generated-problems))
        all-results {:templates   templates
                     :problem-map problem-map}]
    (cspit (str target "problems.edn")
           (->> (mapv #(dissoc % :problems) results)
                (mapv #(merge % (:meta %)))
                (mapv #(dissoc % :meta))
                (mapv #(assoc % :edn-file (str (first (cs/split (:filename %) #"\.")) ".edn")))))
    (doseq [{:keys [filename problems]} results]
      (let [edn-name (str (first (cs/split filename #"\.")) ".edn")]
        (cspit (str target "problems/" edn-name)
               (->> (mapv #(merge % (:meta %)) problems)
                    (mapv #(dissoc % :meta))))))
    all-results))

(defn- grab
  "Grab the file for one problem template, and process it"
  [source {:keys [folder file gen-fn]}]
  (let [res-folder (str source folder "/" file)
        problem-string (slurp res-folder)
        problem-strings (cs/split problem-string #"==sepa==")
        mapi {:gen-fn gen-fn
              :file   res-folder
              :filename file}]
    (if (= 2 (count problem-strings))
      (let [problem-meta {:template-id (uuid)
                          :level-id    (uuid)
                          :generated?  true
                          :topic       folder}
            new-problem-string (str "\n" (cstr problem-meta) "\n"
                                    "==sepa==" "\n"
                                    problem-string)]
        (spit res-folder new-problem-string)
        (merge mapi
               {:meta    problem-meta
                :soal    (problem-strings 0)
                :bahasan (problem-strings 1)}))
      (merge mapi
             {:meta    (edn/read-string (problem-strings 0))
              :soal    (problem-strings 1)
              :bahasan (problem-strings 2)}))))




