(ns alfa.installer.core
  (:require
    [me.raynes.fs :as fs]
    [alfa.utils :refer :all]
    [com.stuartsierra.component :as component]
    [alfa.grabber.component :as grab]
    [alfa.installer.system :refer :all]))

(defn make-libs-map
  "Create an installer map from content mode"
  [mode]
  (let [cpath (main-path :vector)]
    {:libs-source (create-path
                    (concat cpath (fs/split "viewer/alfa/src/clj/alfa/libs")))
     :libs-target (create-path
                    (concat cpath (fs/split "app/alfa/src/clj/alfa/libs")))
     :libs-test-source (create-path
                         (concat cpath (fs/split "viewer/alfa/test/alfa/libs")))
     :libs-test-target (create-path
                         (concat cpath (fs/split "app/alfa/test/alfa/libs")))
     :mode        mode}))

(defn copy-libs
  [{:keys [libs-source libs-target
           libs-test-source libs-test-target]
    :as installer}]
  (fs/copy-dir-into libs-source libs-target)
  (fs/copy-dir-into libs-test-source libs-test-target))

(defn -main [& [args circle-ci?]]
  (info "We're storing the " args " content into database!")
  (let [mode (keyword args)
        sys (when-not circle-ci?
              (-> mode create-installer component/start-system))]
    (info "We're storing the " args " content into files")
    (when-not circle-ci?
      (time (grab/spit-contents! (:grabber sys))))
    (info "Time taken to create the content files")
    (info "Copying viewer.alfa.libs into app.alfa.libs")
    (copy-libs (make-libs-map mode))
    sys))

