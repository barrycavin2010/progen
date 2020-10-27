(ns tree
  (:require [alfa.utils :refer :all]
            [me.raynes.fs :as fs]))

(defn vidsource
  ([] (-> (->> (fs/split fs/*cwd*)
               rest
               (drop-last 2))
          (concat ["content" "video"])))
  ([_] (create-path (vidsource))))

(defn vidprod
  ([] (-> (->> (fs/split fs/*cwd*)
               rest
               (drop-last 2))
          (concat ["content" "video-production"])))
  ([_] (create-path (vidprod))))

(defn vid-path
  [fname]
  (let [fook (str (first fname))]
    (path (vidprod true) fook)))

(defn copy-to-path
  [fname]
  (let [vpath (vid-path fname)]
    (-> (vidsource true)
        (path fname)
        (fs/copy+ (path vpath fname)))))

(defn copy-videos
  []
  (doseq [v (fs/list-dir (vidsource true))]
    (copy-to-path (fs/base-name v))))

(defn call-video
  [fname]
  (let [vpath (vid-path fname)]
    (fs/exists? (path vpath fname))))

(defn check-videos
  []
  (->> (fs/list-dir (vidsource true))
       (map (comp call-video fs/base-name))
       (every? true?)))


