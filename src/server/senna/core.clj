(ns senna.core
  (:require
   [clojure.java.jdbc :as jdbc]
   [clojure.pprint :refer [pprint]]
   [compojure.core :refer [defroutes POST GET]]
   [compojure.route :refer [resources not-found]]
   [ring.middleware.params :refer [wrap-params]]
   [senna.index :refer [index-page]])
  (:gen-class))

(def mysql-db {:subprotocol "mysql"
               :subname "//127.0.0.1:3306/senna"
               :user "root"
               :password "rootpassword"})

(defn- random-questions [req]
  (let [r (jdbc/query mysql-db ["select * from questions"])]
    (pprint r)
    r))

(defroutes app-routes
  (GET "/" [] index-page)
  (GET "/questions" [] random-questions)
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
      wrap-params))

(defn init []
  (println "initializing data"))
