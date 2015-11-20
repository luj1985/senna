(ns senna.core
  (:require
   [compojure.core :refer [defroutes ANY GET]]
   [compojure.route :refer [resources not-found]]
   [ring.middleware.params :refer [wrap-params]]
   [ring.middleware.edn :refer (wrap-edn-params)]
   [ring.util.response :refer (redirect)]))


(defroutes routes
  (resources "/")
  (resources "/public")
  (resources "/" {:root "/META-INF/resources"})
  (GET "/questions" []
       "return JSON formatted question"))

(def handler
  (-> routes
      wrap-params
      wrap-edn-params))

(defn init []
  (println "initializing data"))
