(ns alfa.webapp.routes
  (:require
    [compojure.core :refer [GET POST context routes]]
    [compojure.route :refer [resources files not-found]]
    [com.stuartsierra.component :as component]
    [ring.util.response :as resp]
    [alfa.utils :refer :all]
    [taoensso.timbre :as log]
    [me.raynes.fs :as fs]
    [alfa.webapp.pages :as page]
    [cheshire.core :as ches]
    [alfa.webapp.data-creator :as creator]
    [noir.response :as nresp]))

(declare resources-routes front-routes api-routes other-routes)

(defn main-routes
  [content]
  (routes (resources-routes content)
          (front-routes content)
          (context "/api" req (api-routes content))
          (other-routes)))

(defn other-routes
  []
  (routes
    (resources "/")
    (not-found "<center><h1>Nothing to see here</h1></center>")))

(defn front-routes
  [content]
  (routes
    (GET "/" req (page/home))
    (GET "/mobile" req (page/small @(get-in content [:data :resources])))
    (GET "/desktop" req (page/large @(get-in content [:data :resources])))))

(defn api-routes
  [content]
  (routes
    (GET "/cg-details/:id" [id]
      (nresp/edn (creator/cg-details content id)))
    (GET "/cg-details-by-child/:id" [id]
      (nresp/edn (creator/cg-details-by-child content id)))
    (GET "/container-details/:id" [id]
      (if-let [res (creator/container-details content id)]
        (nresp/edn res)
        (resp/not-found "Not found")))
    (GET "/generate-zp-problem/:id" [id]
      (nresp/edn (creator/generate-zp-problem content id)))
    (GET "/update-content" req
      (do (creator/update-content! content)
          (Thread/sleep 10000)
          (nresp/edn (creator/cg-details content "top"))))
    (GET "/generate-old-problem/:id" [id]
      (nresp/edn (creator/generate-old-problem content id)))))

(defn resources-routes
  [content]
  (let [res-dirs (:resources content)]
    (routes
      (GET "/image/:filename" [filename]
        (resp/file-response (-> (:image res-dirs)
                                (path filename))))
      (GET "/znet_problem/:filename" [filename]
        (resp/file-response (-> (:znet-problem res-dirs)
                                (path filename))))
      (GET "/video/:filename" [filename]
        (resp/file-response (-> (get-in content [:grabber :video-dir])
                                (path filename))))
      (GET "/resources/:filename" [filename]
        (resp/file-response (-> (:resources res-dirs)
                                (path filename)))))))
