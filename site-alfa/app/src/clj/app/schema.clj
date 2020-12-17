(ns app.schema
  (:require [schema.core :as s]))

;; Viewer app schemas

;; Site app schemas

(comment
  "Required refs"

  "Content entities"
  :template-ids
  :templates [:math :english :logic]
  :problem-map "Big map from template-id to list of problem-ids for that template"
  :problems "All problems keyed by problem-id")

(def Template
  {:template-id s/Str
   :edn-file    s/Str
   :topic       s/Str})

(def Templates
  {:math    [Template]
   :english [Template]
   :logic   [Template]})

(def SortedTemplate
  {:template-id s/Str
   :submission  s/Int
   :right       s/Int
   :wrong       s/Int
   :called      s/Int})

;; Content schemas

(def cg-schema
  "Schema for content-group, it has first-level children only, but all parents."
  {:id            s/Str
   :title         s/Str
   :level         s/Int
   :sequence      s/Int
   :description   s/Str
   :url-title     s/Str
   :cg-children   [{:id s/Str :title s/Str :sequence s/Int}]
   :cg-parents    [{:id s/Str :title s/Str :distance s/Int}]
   :cg-containers [{:id s/Str :title s/Str :sequence s/Int}]})

(def video-schema
  {:sequence s/Int
   :id       s/Str
   :nama     s/Str
   :filename s/Str
   :con-id   s/Str})

(def notes-schema
  {:sequence                 s/Int
   :id                       s/Str
   :text                     s/Str
   :con-id                   s/Str
   :title                    s/Str
   (s/optional-key :script)  s/Any
   (s/optional-key :creator) s/Str})

(def znet-problem-schema
  {:sequence s/Int
   :id       s/Str
   :answer   s/Str
   :image    s/Str})

(def instance-zp-problem-schema
  {:pembahasan               s/Str
   :header                   s/Str
   :type                     s/Keyword
   :set?                     s/Bool
   (s/optional-key :soal)    s/Any
   (s/optional-key :id)      s/Str
   (s/optional-key :soals)   [s/Any]
   (s/optional-key :answers) [s/Any]
   (s/optional-key :answer)  s/Any
   (s/optional-key :sebab)   s/Str
   (s/optional-key :akibat)  s/Str
   (s/optional-key :script)  s/Any})

(def zp-problem-schema
  {:id                         s/Str
   :pembahasan                 s/Str
   :soal                       s/Str
   :coder                      s/Str
   :creator                    s/Str
   :set?                       s/Bool
   :type                       s/Keyword
   :title                      s/Str
   :dir                        s/Str
   (s/optional-key :bener?)    s/Bool
   (s/optional-key :generator) s/Str
   (s/optional-key :test)      s/Str
   (s/optional-key :answers)   s/Str
   (s/optional-key :relations) s/Str
   (s/optional-key :script)    s/Any})

(def con-zp-problem-schema
  {:id       s/Str
   :con-id   s/Str
   :title    s/Str
   :sequence s/Int})

(def container-schema
  {:id                                 s/Str
   :title                              s/Str
   :sequence                           s/Int
   :description                        s/Str
   :url-title                          s/Str
   :cg-id                              s/Str
   :playlist                           s/Any
   :temp-dir                           s/Str
   (s/optional-key :con-videos)        [video-schema]
   (s/optional-key :coder)             s/Str
   (s/optional-key :con-znet-problems) [znet-problem-schema]
   (s/optional-key :con-zp-problems)   [con-zp-problem-schema]
   (s/optional-key :con-notes)         [(dissoc notes-schema :text)]})

(def config-schema
  {:content-source [s/Str]
   :video-target   [s/Str]
   :lib-source     s/Str
   :content-target {:entry        [s/Str]
                    :db-data      s/Str
                    :znet-problem s/Str
                    :zp-problem   s/Str
                    :old-problem  s/Str
                    :video        s/Str
                    :notes        s/Str
                    :image        s/Str
                    :resources    s/Str}
   :server         {:port s/Int
                    :path s/Str
                    :host s/Str}})

(def content-source-schema
  (:content-source config-schema))

(def content-target-schema
  (:content-target config-schema))

(def content-state-schema
  {:content-group s/Any
   :container     s/Any
   :znet-problem  s/Any
   :zp-problem    s/Any
   :old-problem   s/Any
   :notes         s/Any
   :video         s/Any
   :image         s/Any
   :resources     s/Any
   :zp-problem-id s/Any})

(def pre-library-schema
  {:lib-source s/Str})

(def library-schema
  (merge pre-library-schema
         {:generate-fn s/Any}))

(def grabber-schema
  {:content-source content-source-schema
   :content-target content-target-schema
   :content-state  content-state-schema
   :video-target   [s/Str]
   :video-dir      s/Str
   :source-dir     s/Str
   :mode           s/Keyword
   :source-entry   [s/Str]
   :target-entry   [s/Str]
   :library        library-schema
   :target-dirs    (dissoc (:content-target config-schema) :entry)
   :generate-fn    s/Any})

(def pre-grabber-schema
  {:content-source s/Any
   :content-target s/Any
   :video-target   [s/Str]
   :library        s/Any
   :content-state  content-state-schema
   :mode           s/Keyword})

(def pre-content-schema
  {:grabber s/Any})

(def content-schema
  {:grabber     grabber-schema
   :data        content-state-schema
   :top-cgs     [cg-schema]
   :resources   s/Any
   :generate-fn s/Any})

(def pre-handler-schema
  {:content s/Any})

(def pre-server-schema
  {:port    s/Int
   :path    s/Str
   :handler s/Any
   :host    s/Str})

(def handler-schema
  {:content content-schema
   :routes  s/Any})

(def server-schema
  (assoc pre-server-schema
    :handler handler-schema
    :stop-fn s/Any))

(def pre-system-schema
  {:mode           s/Keyword
   :library        pre-library-schema
   :content-source content-source-schema
   :content-target content-target-schema
   :server         pre-server-schema
   :handler        pre-handler-schema
   :grabber        pre-grabber-schema
   :content        pre-content-schema})

(def post-system-schema
  {:mode           s/Keyword
   :content-source content-source-schema
   :content-target content-target-schema
   :grabber        grabber-schema
   :server         server-schema
   :library        library-schema
   :handler        handler-schema
   :content        content-schema})

(def stopped-system-schema
  (update-in post-system-schema [:server] #(dissoc % :stop-fn)))



