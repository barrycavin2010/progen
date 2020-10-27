(ns alfa.libs
  (:require
    [com.stuartsierra.component :as component]
    [alfa.utils :refer :all]
    [alfa.libs.core :refer :all]))

(defrecord Library [lib-source]
  component/Lifecycle
  (start [this]
    (assoc this
      :generate-fn
      (fn [fn-str]
        (try (binding [*ns* (the-ns 'alfa.libs.core)]
               (eval (read-string fn-str)))
             (catch Exception e
               (error "Generator ERROR caused by : "
                      (.getMessage e))
               (throw (Exception. "Generator error")))))))
  (stop [this]
    this))

(defn make [lib-source]
  (map->Library {:lib-source lib-source}))
