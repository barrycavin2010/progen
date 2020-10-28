(ns app.content.component
  (:require
    [com.stuartsierra.component :as component]
    [app.engines.view :as view]
    [app.engines.produce :as producer]
    [app.utils :refer :all]
    [me.raynes.fs :as fs]))

(defrecord Content [source max-view soal-choice]
  component/Lifecycle
  (start [this]
    (if soal-choice
      (assoc this :soals (view/generate {:source      source
                                         :max-view    max-view
                                         :soal-choice soal-choice}))
      (assoc this :soals (producer/generate {:source source
                                             :max-view max-view}))))
  (stop [this]
    (dissoc this :soals)))

(defn make [content-config]
  (map->Content content-config))


