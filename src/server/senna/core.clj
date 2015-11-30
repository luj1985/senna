(ns senna.core
  (:require
   [clojure.pprint :refer [pprint]]
   [clojure.java.jdbc :as jdbc]
   [compojure.core :refer [defroutes POST GET]]
   [compojure.route :refer [resources not-found]]
   [ring.middleware.params :refer [wrap-params]]
   [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
   [ring.middleware.keyword-params :refer [wrap-keyword-params]]
   [ring.middleware.nested-params :refer [wrap-nested-params]]
   [ring.middleware.cookies :refer [wrap-cookies]]
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

(defn- generate-random-uid []
  (str (java.util.UUID/randomUUID)))

(defn- extract-uid [request]
  (get-in request [:cookies "uid" :value] ))

(defn- create-user! []
  (let [uid (generate-random-uid)]
    (jdbc/insert! mysql-db :users {:uid uid})
    uid))

(defn- save-score! [uid score]
  (jdbc/insert! mysql-db :results {:uid uid :result score}))

(defn- random-questions [request]
  (let [uid (extract-uid request)
        res (response (jdbc/query mysql-db ["select * from questions"]))]
    (if uid
      res
      (let [new-id (create-user!)]
        (assoc res :cookies {:uid new-id})))))

(defn- search-rank [score]
  (let [rs (jdbc/query mysql-db ["select count(id) as rank from results where result < ?" score])]
    (-> (first rs)
        (get :rank)
        ;; start from 0
        (inc))))

(defn- search-best [uid]
  (let [rs (jdbc/query mysql-db ["select count(id) as rank from results where result < (select min(result) from results where uid=?)" uid])]
    (-> (first rs)
        (get :rank)
        (inc))))

(defn- rank-score [request]
  (let [uid (or (extract-uid request) (create-user!))
        score (get-in request [:body "score"])]

    (save-score! uid score)

    (let [rank (search-rank score)
          best (search-best uid)]
      (response {:global rank
                 :best best}))))

(defroutes app-routes
  (GET "/" [] index-page)
  (GET "/questions" [] random-questions)
  (POST "/score" [] rank-score)
  (resources "/")
  (not-found "Page not found"))

(def handler
  (-> app-routes
      wrap-keyword-params
      wrap-json-response
      wrap-json-body
      wrap-params
      wrap-nested-params
      wrap-cookies))


(defn init []
  (println "initializing data"))
