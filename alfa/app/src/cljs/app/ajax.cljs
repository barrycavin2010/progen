(ns app.ajax
  (:require
    [re-frame.core :as re]
    [app.utils :as u :refer [info re-render-mathjax]]
    [ajax.core :as ajax :refer [GET POST ajax-request]]
    [ajax.edn :as edn]))




