(ns app.webapp.routes
  (:require
    [compojure.core :refer [GET POST context routes]]
    [compojure.route :refer [resources files not-found]]
    [ring.util.response :as resp]
    [app.utils :refer :all]
    [app.webapp.pages :as page]
    [noir.response :as nresp]))

(declare front-routes api-routes other-routes)

(defn main-routes
  [content]
  (routes (front-routes content)
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
    (GET "/desktop" req (page/large "A Message"))))

(defn api-routes
  [content]
  (routes
    (GET "/api-test/:id" [id]
      (nresp/edn {:text (str "This is a nice problem to have : " id)}))))

