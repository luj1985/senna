(ns senna.core
  (:require
   [clojure.pprint :refer [pprint]]
   [clojure.java.jdbc :as jdbc]
   [compojure.core :refer [defroutes POST GET]]
   [compojure.route :refer [resources not-found]]
   [ring.middleware.params :refer [wrap-params]]
   [ring.middleware.json :refer [wrap-json-response]]
   [ring.middleware.keyword-params :refer [wrap-keyword-params]]
   [ring.util.response :refer [response]]
   [senna.index :refer [index-page]])
  (:gen-class))

;; TODO: read password from environment variable ?
(def mysql-db {:subprotocol "mysql"
               :subname "//127.0.0.1:3306/senna"
               :user "root"
               :password "vote*demo"})

(defn- dump-request-params [handler]
  (fn [request]
    (pprint request)
    (handler request)))

(defn- random-questions [_]
  (response
   (jdbc/query mysql-db ["select * from questions"])))

(defn- rank-score [body]
  (println body)
  (response {}))

(defroutes app-routes
  (GET "/" [] index-page)
  (GET "/questions" [] random-questions)
  (POST "/score" {body :body} (rank-score body))
  (resources "/")
  (not-found "Page not found"))

(def handler
  (-> app-routes
      wrap-keyword-params
      wrap-json-response
      wrap-params))


(defn init []
  (println "initializing data"))
