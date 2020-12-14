(ns app.site.routes
  (:require
    [compojure.core :refer [GET POST context routes]]
    [compojure.route :refer [resources files not-found]]
    [ring.util.response :as resp]
    [cheshire.core :as cc]
    [app.utils :refer :all]
    [app.site.pages :as page]
    [noir.response :as nresp]))

(declare front-routes api-routes other-routes)

(defn main-routes
  [dbase]
  (routes (front-routes dbase)
          (context "/api" req (api-routes dbase))
          (other-routes)))

(defn other-routes
  []
  (routes
    (resources "/")
    (not-found "<center><h1>Nothing to see here</h1></center>")))

(defn front-routes
  [dbase]
  (routes
    (GET "/" req (page/site dbase))))

(defn api-routes
  [dbase]
  (routes
    (GET "/get-templates" req
      (nresp/edn (get-in dbase [:templates])))))

