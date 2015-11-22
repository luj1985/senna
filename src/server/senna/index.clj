(ns senna.index
  (:use
   [hiccup.page]))

(defn index-page [_]
  (html5
   [:head
    [:meta {:http-equiv "content-type" :content "text/html; charset=utf-8"}]
    [:meta {:http-equiv "x-ua-compatible" :content "ie=edge"}]
    [:meta {:name "description" :content ""}]
    [:meta {:name "keywords" :content ""}]
    [:meta {:name "viewport"
            :content "width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no"}]
    [:meta {:name "renderer" :content "webkit"}]
    [:title "小车跑跑跑"]
    [:link {:href "css/garden.css"
            :rel "stylesheet"
            :type "text/css"
            :media "screen"}]]

   [:body
    [:div#main
     ;; This "loading" html fragment will be replaced by React loader component
     ;; after javascript file was loaded.
     ;; Here is used to prevent initial blank page.
     [:div#loading
      [:div.logo]
      [:div.progress-bar
       [:progress {:max 100 :value 1}]]]]
    [:div#dialog]
    (include-js "js/app.js")]))
