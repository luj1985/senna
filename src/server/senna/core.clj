(ns senna.core
  (:require
   [clojure.pprint :refer [pprint]]
   [compojure.core :refer [defroutes ANY GET]]
   [compojure.route :refer [resources not-found]]
   [ring.middleware.params :refer [wrap-params]]
   [ring.middleware.edn :refer [wrap-edn-params]]
   [com.ebaxt.ring-rewrite :refer [wrap-rewrite]]
   [ring.util.response :refer [redirect]])
  (:gen-class))


(defroutes app-routes
  (resources "/")
  (not-found "Page not found"))


(defn dump-request [request]
  (pprint request))

(defn wrap-dump [handler]
  (fn [request]
    (dump-request request)
    (handler request)))

(def handler
  (-> app-routes
      wrap-dump
      (wrap-rewrite
       [:rewrite "/" "/index.html"]
       #_[:rewrite #"/senna/?$" "/index.html"]
       #_[:rewrite #"/senna/(.+)" "$1"])
      wrap-params))

(defn init []
  (println "initializing data"))
