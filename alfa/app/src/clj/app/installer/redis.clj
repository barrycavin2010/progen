(ns alfa.installer.redis
  (:require
    [com.stuartsierra.component :as component]
    [alfa.utils :refer :all]
    [taoensso.carmine :as car :refer [wcar]]))

(defrecord Redis [content]
  component/Lifecycle
  (start [this]
    (let [conn {:pool {} :spec {}}
          refs (get-in content [:data])
          mode (get-in content [:grabber :mode])]
      (info "Storing data into redis")
      (time (wcar conn
                  (car/set (str (name mode) "-" "content-group") (str @(:content-group refs)))
                  (car/set (str (name mode) "-" "count-content-group") (count @(:content-group refs)))
                  (car/set (str (name mode) "-" "container") (str @(:container refs)))
                  (car/set (str (name mode) "-" "count-container") (count @(:container refs)))
                  (car/set (str (name mode) "-" "znet-problem") (str @(:znet-problem refs)))
                  (car/set (str (name mode) "-" "count-znet-problem") (count @(:znet-problem refs)))
                  (car/set (str (name mode) "-" "zp-problem") (str @(:zp-problem refs)))
                  (car/set (str (name mode) "-" "count-zp-problem") (count @(:zp-problem refs)))
                  (car/set (str (name mode) "-" "old-problem") (str @(:old-problem refs)))
                  (car/set (str (name mode) "-" "count-old-problem") (count @(:old-problem refs)))
                  (car/set (str (name mode) "-" "notes") (str @(:notes refs)))
                  (car/set (str (name mode) "-" "count-notes") (count @(:notes refs)))
                  (car/set (str (name mode) "-" "video") (str @(:video refs)))
                  (car/set (str (name mode) "-" "count-video") (count @(:video refs)))
                  (car/set (str (name mode) "-" "image") (str @(:image refs)))
                  (car/set (str (name mode) "-" "count-image") (count @(:image refs)))
                  (car/set (str (name mode) "-" "resources") (str @(:resources refs)))
                  (car/set (str (name mode) "-" "count-resources") (count @(:resources refs)))))
      (info "Data has been stored into redis")
      (assoc this :conn conn)))
  (stop [this] this))

(defn make [] (map->Redis {}))

