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
   [ring.util.response :refer [response redirect]]
   [senna.index :refer [index-page]]
   [senna.brands :refer [brands-page brand-page]]
   [senna.prize :refer [prize-page]]
   [senna.dashboard :refer [dashboard-page]])
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
  (let [headers (:headers request)]
    (get headers "uid")))

(defn- create-user! []
  (let [uid (generate-random-uid)]
    (jdbc/insert! mysql-db :users {:uid uid})
    uid))

(defn- save-score! [uid score]
  (jdbc/insert! mysql-db :results {:uid uid :result score}))

(defn- random-questions [request]
  (let [uid (or (extract-uid request) (create-user!))
        res (response (jdbc/query mysql-db ["select * from questions"]))]
    (assoc res :headers {"UID" uid})))

(defn- search-rank [score]
  (let [rs (jdbc/query mysql-db ["select count(id) as rank from results where result < ?" score])]
    (-> (first rs)
        (get :rank)
        ;; start from 0
        (inc))))

(defn- search-best [uid]
  (let [rs (jdbc/query mysql-db ["select min(result) as best from results where uid=?" uid])]
    (-> (first rs)
        (get :best)
        (inc))))

(defn- rank-score [request]
  (let [uid (or (extract-uid request) (create-user!))
        score (get-in request [:body "score"])]
    (save-score! uid score)
    (let [rank (search-rank score)
          best (search-best uid)]
      (response {:global rank :best best}))))

(defn- save-mobile-number [request]
  (let [number (get-in request [:body "number"])
        uid (or (extract-uid request) (create-user!))]
    (jdbc/update! mysql-db :users {:mobile number} ["uid = ?" uid])
    (response {:uid uid :mobile number})))

(defn- render-dashboard [request]
  ;; select mobile, result from users, results where users.uid = results.uid group by users.uid
  (let [rs (jdbc/query mysql-db ["select mobile, best from users a, (select uid, min(result) as best from results group by uid) b where a.uid = b.uid order by best asc"])]
    (dashboard-page rs)))

(defroutes app-routes
  (GET "/" [] index-page)
  (GET "/questions" [] random-questions)
  (GET "/brands" [] brands-page)
  (GET "/brands/:id" [id] (brand-page id))
  (POST "/mobile" [] save-mobile-number)
  (GET "/prizes" [] prize-page)
  (POST "/score" [] rank-score)
  (GET "/_dashboard" [] render-dashboard)
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
