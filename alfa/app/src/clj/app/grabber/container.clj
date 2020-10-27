(ns alfa.grabber.container
  (:require
    [clojure.string :as cs]
    [me.raynes.fs :as fs]
    [alfa.utils :refer :all]
    [alfa.problem.grabber :as zpro]
    [alfa.schema :refer :all]
    [clojure.set :as cst]
    [schema.core :as s]))

(declare register-videos! register-znet-problems!
         register-playlist! copy-images!
         register-notes!)

(defn container?
  "Returns true if a dir is a container, which indicated by the
  existence of container.edn"
  [dir]
  (boolean
    (and (fs/directory? dir)
         (let [children (fs/list-dir dir)
               valchild (-> #(= "container.edn" (fs/base-name %))
                            (filter children))]
           (and (not-empty valchild)
                (or (let [res (try (cslurp (first valchild))
                                   (catch Exception e
                                     (error "Unreadable container.edn" (conpath dir))
                                     (error "container.edn error caused by" (.getMessage e))))]
                      (when (map? res)
                        (if (:coder res)
                          (let [resi
                                (cst/difference
                                  (set (keys res))
                                  (cst/union (set (keys container-schema))
                                             #{:coder :creator :con-znet-problems
                                               :con-videos :con-zp-problems
                                               :con-notes}))]
                            (if (empty? resi)
                              true
                              (do (collect-error (:coder res "Someone"))
                                  (error (:coder res "Someone")
                                         "thinks it's a good idea to copy problem meta.edn as container.edn in"
                                         (conpath dir)))))
                          (error "Container should have a key :coder" (conpath dir)))))
                    (error "Invalid container.edn for" (conpath dir))))))))

(comment
  (defn- addhoc-rename
    "Helper function to rename old ways of doing things."
    [dir-file]
    (let [base (fs/base-name dir-file)]
      (when (#{"zproblem" "zproblem.edn"} base)
        (->> [base]
             (concat (butlast (fs/split dir-file)))
             create-path
             (fs/rename dir-file))))))

(defn register!
  "Register a container by collecting its meta and add uuid
  if it's not yet present in meta."
  [dir cg-id sequ]
  (let [base-name (fs/base-name dir)
        meta-file (path dir "container.edn")
        title (->> (drop 3 base-name)
                   (apply str))
        meta (cslurp meta-file)]
    (when (or (nil? (:playlist meta))
              (not (vector? (:playlist meta))))
      (collect-error (:coder meta "Someone"))
      (error (:coder meta "Someone") "'s" "playlist in container.edn must exist and must be a vector" (conpath dir)))
    (when (fs/exists? (path dir "zp-problem"))
      (collect-error (:coder meta "Someone"))
      (error (:coder meta "Someone") "made an error in container folder")
      (error "It should be zp_problem folder not zp-problem, files in this folder won't be copied.." (conpath dir)))
    (when (fs/exists? (path dir "zp_problems"))
      (collect-error (:coder meta "Someone"))
      (error (:coder meta "Someone") "made an error in container folder")
      (error "It should be zp_problem folder not zp_problems, files in this folder won't be copied.." (conpath dir)))
    (when (fs/exists? (path dir "znet-problem"))
      (collect-error (:coder meta "Someone"))
      (error (:coder meta "Someone") "made an error in container folder")
      (error "It should be znet_problem folder not znet-problem, files in this folder won't be copied.." (conpath dir)))
    (when (fs/exists? (path dir "znet_problems"))
      (collect-error (:coder meta "Someone"))
      (error (:coder meta "Someone") "made an error in container folder")
      (error "It should be znet_problem folder not znet_problems, files in this folder won't be copied.." (conpath dir)))
    (merge meta
           {:id                (:id meta (uuid))
            :playlist          (:playlist meta [])
            :title             title
            :url-title         (cs/replace title #" " "_")
            :sequence          sequ
            :temp-dir          (str dir)
            :con-videos        []
            :con-znet-problems []
            :con-zp-problems   []
            :con-notes         []
            :cg-id             cg-id})))

(defn grab-and-register!
  "Grab the container with all its content and register the meta"
  [gen-fn refs target-dirs dir cg-id sequ]
  (let [result (register! dir cg-id sequ)
        ref-content @(:container refs)
        res (ref-content (:id result))]
    (if (nil? res)
      (do (dosync (alter (:container refs) #(assoc % (:id result) result)))
          (register-videos! result refs (:id result) dir)
          (register-znet-problems! result refs target-dirs (:id result) dir)
          (copy-images! result refs target-dirs dir)
          (register-notes! result refs (:id result) dir)
          (zpro/register-zp-problems! result gen-fn refs (:id result) (path dir "zp_problem"))
          (register-playlist! result refs (:id result) dir)
          result)
      (error "Someone think it's a good idea to copas the container resulting in duplicate uuid in"
             (conpath dir)
             "with"
             (conpath (:temp-dir res))))))

(defn- register-videos!
  "Reading the video.edn and register the videos into the container"
  [meta refs con-id dir]
  (when-let [videos (when (fs/exists? (path dir "video.edn"))
                      (try (cslurp (path dir "video.edn"))
                           (catch Exception e
                             (error "Error while reading video.edn in " (conpath dir))
                             (error "This error presented by " (:coder meta "Someone"))
                             (error "video.edn reading error caused by " (.getMessage e)))))]
    (if (and (vector? videos)
             (every? vector? videos)
             (every? #(every? string? %) videos))
      (let [data (-> #(let [[a b] %2] {:id a :sequence %1 :nama (cs/capitalize b) :filename (str a ".mp4") :con-id con-id})
                     (map-indexed videos))]
        (dosync (alter (:video refs) #(merge % (zipmap (map :id data) data)))
                (alter (:container refs)
                       (fn [c]
                         (->> #(assoc % :con-videos (vec (concat (:con-videos %) data)))
                              (update-in c [con-id]))))))
      (do (error "Error while reading video.edn in " (conpath dir))
          (error "This error presented by " (:coder meta "Someone"))))))

(defn- znet-problems
  "Returns the file paths for problems if a directory is a container with problem,
  nil otherwise."
  [dir meta]
  (when (fs/exists? (path dir "znet_problem.edn"))
    (when-let [znetproblems (try (cslurp (path dir "znet_problem.edn"))
                                 (catch Exception e
                                   (error "Error while reading znet_problem.edn in"
                                          (conpath dir))
                                   (error "This error presented by" (collect-error (:coder meta "Someone")))
                                   (error "znet_problem.edn error caused by" (.getMessage e))))]
      (if (and (vector? znetproblems)
               (every? vector? znetproblems)
               (every? #(and (string? (first %))
                             (string? (second %)))
                       znetproblems)
               (not-empty znetproblems))
        (let [imgs (->> (fs/list-dir (path dir "znet_problem"))
                        (filterv #(#{".jpg" ".JPG" ".PNG" ".GIF" ".png" ".gif"} (fs/extension %))))]
          (loop [[img & imgss] (map fs/base-name imgs)]
            (when img
              (fs/rename (-> (path dir "znet_problem")
                             (path img))
                         (-> (path dir "znet_problem")
                             (path (str (fs/name img) (cs/lower-case (fs/extension img))))))
              (recur imgss)))
          (->> (fs/list-dir (path dir "znet_problem"))
               (filterv #(#{".jpg" ".gif" ".png"} (fs/extension %)))))
        (do (error "znet_problem.edn must be a vector of vectors" (conpath dir))
            (error "This error presented by" (collect-error (:coder meta "Someone"))))))))

(defn- register-znet-problems!
  "Reading from container zproblem folder, and build problems data for that container."
  [meta refs target-dirs con-id dir]
  (when-let [zproblems (znet-problems dir meta)]
    (let [target (:znet-problem target-dirs)
          data (let [propos (cslurp (path dir "znet_problem.edn"))
                     ffs (fn [p] (try (fs/extension (first (filter #(= p (fs/name %)) zproblems)))
                                      (catch Exception e (error "No znetproblem file found") ".jpg")))]
                 (->> (loop [[ika & ikas]
                             (->> propos
                                  (map-indexed #(let [[a b] %2] [%1 a b]))
                                  (mapv #(conj %2 %1) (mapv #(let [[a] %] (str a (ffs a))) propos)))
                             res []]
                        (if ika
                          (let [[i pid k fname] ika]
                            (recur
                              ikas
                              (conj res
                                    (if-let [resiko (@(:znet-problem refs) pid)]
                                      (error "Duplicated znet-problem uuid by" (collect-error (:coder meta "Someone")) "in" (conpath dir))
                                      (if (string? k)
                                        {:sequence i
                                         :id       pid
                                         :answer   k
                                         :image    fname}
                                        (error (collect-error (:coder meta "Someone"))
                                               "thinks kids could answer a problem if no answer key in"
                                               (conpath dir)))))))
                          res))
                      (keep identity)))]
      (loop [[img & imgss] zproblems]
        (when img
          (fs/copy img (path target (fs/base-name img)))
          (recur imgss)))
      (dosync (alter (:znet-problem refs) #(merge % (zipmap (map :id data) data)))
              (alter (:container refs)
                     (fn [c]
                       (->> #(assoc %
                               :con-znet-problems
                               ;; (concat (:con-znet-problems %) data)
                               (vec data))
                            (update-in c [con-id])))))
      (vec data))))

(defn- copy-images!
  "Copy the images"
  [meta refs target-dirs dir]
  (when (fs/exists? (-> (path dir "zp_problem")
                        (path "images")))
    (error (collect-error (:coder meta "Someone"))
           "created images folder which supposed to be image, files in this folder won't be copied.." (conpath dir)))
  (when (fs/exists? (-> (path dir "notes")
                        (path "images")))
    (error (collect-error (:coder meta "Someone"))
           "created images folder which supposed to be image, files in this folder won't be copied.." (conpath dir)))
  (let [images (not-empty
                 (->> (fs/list-dir (-> (path dir "zp_problem")
                                       (path "image")))
                      (filterv #(#{".jpg" ".png" ".gif"} (fs/extension %)))))
        image-notes (not-empty
                      (->> (fs/list-dir (-> (path dir "notes")
                                            (path "image")))
                           (filterv #(#{".jpg" ".png" ".gif"} (fs/extension %)))))]
    (when-let [total-images (concat images image-notes)]
      (let [target (:image target-dirs)]
        (loop [[img & imgss] total-images]
          (when img
            (fs/copy img (path target (fs/base-name img)))
            (dosync (alter (:image refs) #(conj % (fs/base-name img))))
            (recur imgss)))))))

(defn- notes
  "Returns the notes"
  [dir]
  (not-empty (->> (fs/list-dir (path dir "notes"))
                  (filterv #(#{".htm" ".html"} (fs/extension %))))))

(defn- process-notes
  "Process individual notes, to separate meta, text, and script."
  [dopo notes-file]
  (let [[meta text script] (-> (slurp notes-file)
                               (cs/split #"==sepa=="))]
    (if script
      (when-let [meta-res
                 (try (read-string meta)
                      (catch Exception e
                        (error (collect-error (:coder dopo "Someone"))
                               "made notes meta error, caused by" (.getMessage e))))]
        (when (map? meta-res)
          {:meta   meta-res
           :text   text
           :script script}))
      (if text
        (when-let [meta-res
                   (try (read-string meta)
                        (catch Exception e
                          (error (collect-error (:coder dopo "Someone"))
                                 "made notes meta error, caused by" (.getMessage e))))]
          (when (map? meta-res)
            {:meta meta-res
             :text text}))
        (when meta
          {:meta {} :text meta})))))

(defn- register-notes!
  "Reading the notes folder from container folder. Copy the video folder"
  [meta refs con-id dir]
  (when-let [listdir (->> (notes dir)
                          (map-indexed #(do [%1 %2])))]
    (let [notes-ref (:notes refs)
          notes-list (->> (loop [[idxl & idxls] listdir res []]
                            (if idxl
                              (let [[idx ndir] idxl]
                                (recur
                                  idxls
                                  (conj res
                                        (when-let [notes-res (process-notes meta ndir)]
                                          (-> (dissoc notes-res :meta)
                                              (merge {:id       (:id (:meta notes-res) (uuid))
                                                      :title    (:title
                                                                  (:meta notes-res)
                                                                  (-> (fs/name ndir)
                                                                      (subs 3)))
                                                      :sequence idx
                                                      :con-id   con-id}))))))
                              res))
                          (keep #(try (s/validate notes-schema %)
                                      (catch Exception e
                                        (error (collect-error (:coder meta "Someone")) "made notes error in " (conpath dir))
                                        (error "Notes error caused by" (.getMessage e))))))]
      (dosync (->> #(merge % (zipmap (map :id notes-list) notes-list))
                   (alter notes-ref))
              (->> #(assoc-in % [con-id :con-notes]
                              (mapv (fn [x] (dissoc x :text :script)) notes-list))
                   (alter (:container refs))))
      (vec notes-list))))

(comment
  (defn- videos
    "Returns the videos"
    [meta dir]
    (when (fs/exists? (path dir "videos"))
      (error (collect-error (:coder meta "Someone"))
             "made videos folder which supposed to be video, files in this folder won't be copied.."
             (conpath dir)))
    (if-let [ori (not-empty (->> (fs/list-dir (path dir "video"))
                                 (filterv #(#{".mp4" ".mp3" ".flv"} (fs/extension %)))))]
      [true ori]
      [false (->> (fs/list-dir "resources/mockdata/video")
                  (filterv #(#{".mp4" ".mp3" ".flv"} (fs/extension %))))]))

  (defn- register-videos!
    "Reading from container video folder, build a video map, copy the video folder
    into target directory while target-directory is the con-id."
    [meta refs target-dirs con-id dir]
    (when-let [[status videos] (videos meta dir)]
      (let [target (:video target-dirs)
            data (vec (for [[i vid] (-> #(do [%1 (fs/base-name %2)])
                                        (map-indexed videos))]
                        (let [fname (-> (cs/replace vid #" " "_")
                                        (cs/replace #"-" "_"))]
                          {:sequence i
                           :id       (uuid)
                           :original vid
                           :nama     (->> (cs/split vid #"\.")
                                          first)
                           :filename fname
                           :path     (str con-id "/" fname)
                           :con-id   con-id})))]
        (if status
          (fs/copy-dir (path dir "video") (path target con-id))
          (fs/copy-dir "resources/mockdata/video" (path target con-id)))
        (doseq [{:keys [original filename]} data]
          (fs/rename (-> (path target con-id)
                         (path original))
                     (-> (path target con-id)
                         (path filename))))
        (dosync (alter (:video refs) #(merge % (zipmap (map :id data) data)))
                (doseq [d data]
                  (->> (fn [c]
                         (->> #(assoc % :con-videos (conj (:con-videos %) d))
                              (update-in c [con-id])))
                       (alter (:container refs)))))
        data))))

(defn- pl-helper
  "Just a helper function that close over some states"
  [meta vid-check notes-check zpa vid-in-con zp-in-con notes-in-con type-check refs con-id dir]
  (fn [idx elmt]
    (if (#{:video :znet-problem :notes} elmt)
      (cond (= :video elmt)
            {:type       :video
             :id         (if-let [res (try (vid-in-con @vid-check)
                                           (catch Exception e))]
                           (do (swap! vid-check inc) res)
                           (do (swap! vid-check inc) :error))
             :repetition 1
             :sequence   idx}
            (= :znet-problem elmt)
            {:type       :znet-problem
             :id         (if-let [res
                                  (no-throw
                                    (str (collect-error (:coder meta "Someone"))
                                         " thinks copying a playlist is a good idea resulting in missing znet-problem in playlist")
                                    (conpath dir)
                                    (zp-in-con @zpa))]
                           (do (swap! zpa inc) res)
                           (do (swap! zpa inc) :error))
             :repetition 1
             :sequence   idx}
            :else
            {:type       :notes
             :id         (if-let [res
                                  (no-throw
                                    (str (collect-error (:coder meta "Someone"))
                                         " add a notes in playlist that doesnt exist in folder")
                                    (conpath dir)
                                    (notes-in-con @notes-check))]
                           (do (swap! notes-check inc) res)
                           (do (swap! notes-check inc)
                               :error))
             :repetition 1
             :sequence   idx})
      (if (vector? elmt)
        (if (second elmt)
          {:type       (let [res (first elmt)]
                         (if (type-check res)
                           res
                           (do (error (collect-error (:coder meta "Someone"))
                                      "wrote an anti-establishment content-type that is"
                                      res
                                      "in"
                                      (conpath dir))
                               res)))
           :id         (second elmt)
           :repetition (try (elmt 2) (catch Exception e 1))
           :sequence   idx}
          (error (collect-error (:coder meta "Someone"))
                 "forgot to provide the id for this type of playlist element"
                 (first elmt)
                 "This content type will be got rid of the playlist in" (conpath dir)))
        (error "This type of playlist element must be a vector" (conpath dir))))))

(defn- register-playlist!
  "Processing the playlist, since each element in playlist can be a keyword, a vector,
  or a complete map then those elements need to be processed correctly before getting
  into the database."
  [meta refs con-id dir]
  (let [meta (@(:container refs) con-id)
        vid-in-con (->> meta :con-videos
                        (mapv :id))
        zp-in-con (->> meta :con-znet-problems
                       (mapv :id))
        notes-in-con (->> meta :con-notes
                          (mapv :id))
        vid-check (atom 0)
        notes-check (atom 0)
        zpa (atom 0)
        type-check (fn [t]
                     (boolean (#{:video :old-problem :zp-problem
                                 :znet-problem :notes} t)))
        f (pl-helper meta vid-check notes-check zpa
                     vid-in-con zp-in-con notes-in-con
                     type-check refs con-id dir)
        new-meta (->> (map-indexed f (:playlist meta))
                      (keep identity)
                      vec
                      (assoc meta :playlist))]
    (dosync (alter (:container refs) #(assoc % con-id new-meta)))
    new-meta))



















