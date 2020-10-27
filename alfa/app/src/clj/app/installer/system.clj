(ns alfa.installer.system
  (:require
    [alfa.utils :refer :all]
    [me.raynes.fs :as fs]
    [com.stuartsierra.component :as component]
    [alfa.webapp.content :as content]
    [alfa.libs :as libs]
    [alfa.grabber.component :as grabber]
    [alfa.installer.redis :as red]
    [alfa.config :refer [config]]))

(defn create-installer
  "It creates an installer only system for use in ci environment"
  [mode]
  (let [{:keys [lib-source content-source content-target video-target]}
        (config mode)]
    (component/system-map
      :grabber (-> (grabber/make mode video-target)
                   (component/using [:content-source :content-target :library]))
      :library (libs/make lib-source)
      :content (component/using (content/make) [:grabber])
      :content-source content-source
      :content-target content-target
      :redis (component/using (red/make) [:content])
      :mode mode)))

