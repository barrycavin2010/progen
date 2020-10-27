(ns alfa.grabber.component
  (:require
    [com.stuartsierra.component :as component]
    [me.raynes.fs :as fs]
    [alfa.utils :refer :all]
    [alfa.grabber.cgroup :refer [grab-and-register!]]
    [alfa.grabber.reporting :as rep]))

(declare create-targets! spit-content! update-content! content-report)

(defrecord Grabber [content-source content-target content-state library video-target mode]
  component/Lifecycle
  (start [this]
    (let [cwd (fs/split fs/*cwd*)
          content-entry (drop-last 2 cwd)
          video-dir (create-path (concat content-entry video-target))
          target-entry (concat content-entry (:entry content-target))
          source-entry (concat content-entry content-source)
          source-dir (create-path source-entry)
          target-dirs (create-targets! target-entry content-target)]
      (info "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")
      (info "-------------------Starting to grab files for mode" mode "------------------")
      (info "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX\n")

      ;; reset the perpetrator
      (reset-collector)

      ;; recursively grab the files in the folder for content
      ;; content grabbed includes cg, container, zp-problem, znet-problem, images,
      ;; resources, notes, and videos
      (grab-and-register! (:generate-fn library) content-state source-dir target-dirs)

      ;; Some very important info
      (info "We're copying the files into target directory, it may take a second or several hours")
      (comment (future (do (Thread/sleep 3000)
                           (info "Ahaaa... got you there!!")
                           (Thread/sleep 3000)
                           (info "Ternyata beneran lamaa... :)"))))

      ;; Moves old-problem into content-state
      (info "Time taken to grab old problem")
      (time (dosync (ref-set (:old-problem content-state)
                             (cslurp "resources/old-problem/new-soal.edn"))))


      ;; Special case for old-problem
      (info "Time taken to copy old problem")
      (time (fs/copy "resources/old-problem/new-soal.edn"
                     (path (:db-data target-dirs) "old_problem.edn")))

      ;; Generate xls reports to automate parts of content-maker jobs.
      (-> (concat content-entry ["content" "reports"])
          (create-path)
          (rep/generate-report content-state))

      ;; Inform the content makers
      (content-report content-state)

      (info "Selecting problemz")
      (let [zp-problemo (vals @(:zp-problem content-state))
            data (->> (-> (fn [x] (if-let [res (silent-try (alfa.problem.generator/generate (:generator-fn library) x))]
                                    (let [resi (and (= :single (:type res))
                                                    (not (:set? res))
                                                    (or (every? false? (map first (get-in res [:soal :choices])))
                                                        (< 1 (count (filter true? (map first (get-in res [:soal :choices])))))))]
                                      (if resi
                                        (do (comment (info "Choices" (map first (get-in res [:soal :choices])))
                                                     (info "Type" (:type x) (:set? x))
                                                     (pres res))
                                            true)
                                        false))
                                    false))
                          (filter zp-problemo))
                      (mapv #(select-keys % [:dir :id :type])))]
        (info "Problem count" (count data))
        (cspit "resources/tempo.edn" data))

      ;; Return the component with updated values
      (merge this
             {:source-entry source-entry
              :source-dir   source-dir
              :target-entry target-entry
              :target-dirs  target-dirs
              :video-dir    video-dir
              :generate-fn  (:generate-fn library)})))
  (stop [this]
    (loop [[dir & dirs] (vals (:target-dirs this))]
      (when dir
        (fs/delete-dir dir)
        (recur dirs)))
    (reset-collector)
    this))

(defn content-report
  "Simple reporting to the console"
  [content-state]
  (info "===================Content report after grabbing files===================")
  (loop [[con & conses] [:container :content-group :znet-problem :old-problem
                         :video :zp-problem :notes :image :resources]]
    (when con
      (info "The number of" con "successfully grabbed" (count @(con content-state)))
      (recur conses)))
  (info "=================================EOR====================================="))

(defn make [mode video-target]
  (map->Grabber {:content-state {:content-group (ref {})
                                 :container     (ref {})
                                 :znet-problem  (ref {})
                                 :zp-problem    (ref {})
                                 :old-problem   (ref {})
                                 :video         (ref {})
                                 :notes         (ref {})
                                 :image         (ref [])
                                 :resources     (ref [])
                                 :zp-problem-id (ref [])}
                 :video-target  video-target
                 :mode          mode}))

(defn spit-content!
  "Spit the content of the refs into files."
  [db-data which-content content]
  (let [mapi {:content-group "content_group.edn"
              :container     "container.edn"
              :znet-problem  "znet_problem.edn"
              :old-problem   "old_problem.edn"
              :zp-problem    "zp_problem.edn"
              :video         "video.edn"
              :notes         "notes.edn"
              :image         "image.edn"
              :resources     "resources.edn"}]
    (info "spitting" which-content)

    ;;FIXME need to uncomment if you want the side effect to happen
    (cspit (path db-data (mapi which-content)) content)

    mapi))

(defn spit-contents!
  "Spit all contents into corresponding file"
  [grabber]
  (print "Progress : ")
  (loop [[ctype & ctypes] [:container :content-group :znet-problem
                           :video :zp-problem :notes :image :resources]]
    (when ctype
      (print "XXXXXXX")
      (spit-content! (get-in grabber [:target-dirs :db-data])
                     ctype
                     @(get (:content-state grabber) ctype))
      (recur ctypes)))
  (println "  100% completed"))

(defn create-targets!
  "Create the target directories if they don't exist.
  Obviously a function with important side effect."
  [target-entry content-target]
  (let [targets (->> (dissoc content-target :entry)
                     vals
                     (mapv #(create-path (concat target-entry [%]))))]
    (when-not (every? fs/exists? targets)
      (loop [[dir & dirs] targets]
        (when dir
          (fs/mkdirs dir)
          (recur dirs))))
    (fs/mkdirs (-> (drop-last 2 target-entry)
                   (concat ["reports"])
                   (create-path)))
    (zipmap (keys (dissoc content-target :entry)) targets)))

(defn update-content!
  "Updating the content by re-do all the tasks done by grabber component start."
  [grabber]
  (let [ref-keys #{:content-group :container :znet-problem :zp-problem
                   :old-problem :video :notes :image :resources :zp-problem-id}]
    (info "Updating the content data for mode" (:mode grabber))

    ;; Reset the content-state
    (dosync (loop [[rk & rks] (vec ref-keys)]
              (when rk
                (when-not (#{:old-problem} rk)
                  (if (#{:image :resources :zp-problem-id} rk)
                    (ref-set (get-in grabber [:content-state rk]) [])
                    (ref-set (get-in grabber [:content-state rk]) {})))
                (recur rks))))

    ;; Delete files containing old data
    (loop [[dir & dirs] (vals (:target-dirs grabber))]
      (when dir
        (fs/delete-dir dir)
        (recur dirs)))

    ;; Recreate the target directories
    (create-targets! (:target-entry grabber)
                     (:content-target grabber))

    ;; Grab and register the content-groups and other content
    (grab-and-register! (:generate-fn grabber)
                        (:content-state grabber)
                        (:source-dir grabber)
                        (:target-dirs grabber))

    ;; Some very important information for the content makers
    (do (info "Copying resources files....")
        (future (do (Thread/sleep 3000)
                    (info "You know what they're saying about impatient people??")
                    (info "Their hair will look like Donald Trump's!!!!!!"))))

    ;; Spit the content-state into the coresponding files

    (info "Generating QC2 xls reports...")

    ;; Regenerate the xls reports
    (-> (drop-last 2 (:target-entry grabber))
        (concat  ["reports"])
        (create-path)
        (rep/generate-report (:content-state grabber)))
    grabber))




