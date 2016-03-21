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
   [ring.middleware.basic-authentication :refer [wrap-basic-authentication]]
   [ring.middleware.session :refer [wrap-session]]
   [ring.util.response :refer [response redirect]]

   [senna.index :refer [index-page brands-page brand-page]]
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
  (let [[{id :generated_key}]
        (jdbc/insert! mysql-db :results {:uid uid :result score})]
    id))

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

(defn- search-quantile [score]
  (let [count-rs (jdbc/query mysql-db ["select count(*) as count from results"])
        rank-rs (jdbc/query mysql-db ["select count(*) as count from results where result >= ?" score])
        total (-> (first count-rs) (get :count) double)
        beat (-> (first rank-rs) (get :count) double)]
    (-> (/ beat total)
        (* 10000)
        Math/ceil
        (/ 100))))

(defn- rank-score [request]
  (let [uid (or (extract-uid request) (create-user!))
        score (get-in request [:body "score"])
        token (save-score! uid score)
        best (search-best uid)
        quantile (search-quantile score)
        session (assoc (:session request) :token token)
        rank (search-rank score)]
    (-> (response {:global rank :best best :percent quantile})
        (assoc :session session))))

(defn- save-mobile-number [request]
  (let [number (get-in request [:body "number"])
        brand (get-in request [:body "brand"])
        uid (or (extract-uid request) (create-user!))
        session (:session request)
        token (:token session)]
    (jdbc/update! mysql-db :users {:mobile number} ["uid = ?" uid])
    (if token
      (do
        (jdbc/update! mysql-db :results {:brand brand} ["id = ?" token])
        (let [session (dissoc session :token)]
          (-> (response {:uid uid :mobile number})
              (assoc :session session))))
      (response {:uid uid :mobile number}))))

(defn- to-number [n]
  (if (number? n)
    n
    (Integer/parseInt n)))

(defn- render-dashboard [request]
  (let [{:strs [page size] :or {page 1 size 20}} (:query-params request)
        page (to-number page)
        size (to-number size)
        start (* (dec page) size)]
    ;; select mobile, result from users, results where users.uid = results.uid group by users.uid
    (let [ranking-rs (jdbc/query mysql-db ["select mobile, best from users a,
(select uid, min(result) as best from results group by uid) b
where a.uid = b.uid order by best asc limit ?,?" start size])
          total-rs (jdbc/query mysql-db ["select count(*) as count from users a,
(select uid, min(result) as best from results group by uid) b
where a.uid = b.uid order by best asc"])
          views-rs (jdbc/query mysql-db ["select * from views"])
          results-rs (jdbc/query mysql-db ["select count(*) as count from users, results where users.uid = results.uid"])
          users-rs (jdbc/query mysql-db ["select count(*) as count from (select distinct results.uid from users, results where users.uid = results.uid) t"])
          users-count (-> (first users-rs) (get :count))
          results-count (-> (first results-rs) (get :count))
          total-count (-> (first total-rs) (get :count))
          page-count (Math/ceil (/ (double total-count) (double size)))
          statistics {:users-count users-count
                      :results-count results-count
                      :pages-count (int page-count)
                      :current page
                      :start start
                      :size size
                      :total total-count}]
      (dashboard-page ranking-rs views-rs statistics))))


(defn- render-brand-page [id token]
  (jdbc/execute! mysql-db ["update  views set count = count+1 where id = ?" id])
  (brand-page id token))

(defn authenticate? [username password]
  (and (= username "caf")
       (= password "caf42")))

(def ^:private render-dashboard-with-auth
  (wrap-basic-authentication render-dashboard authenticate?))


(defn- wechat-handler [request]
  (get-in request [:params :echostr]))

(defroutes app-routes
  (GET "/" [] index-page)
  (GET "/wechat" [] wechat-handler)
  (GET "/questions" [] random-questions)
  (GET "/brands" [] brands-page)
  (GET "/brands/:id" {{id :id} :params session :session}
    (let [token (:token session)]
      (render-brand-page (Integer/parseInt id) token)))

  (POST "/mobile" [] save-mobile-number)
  (POST "/score" [] rank-score)

  (GET "/_dashboard" [] render-dashboard-with-auth)

  (resources "/")
  (not-found "Page not found"))

(def handler
  (-> app-routes
      wrap-session
      wrap-keyword-params
      wrap-json-response
      wrap-json-body
      wrap-params
      wrap-nested-params
      wrap-cookies))

(defn init []
  (println "initializing data"))
