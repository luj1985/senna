(ns senna.index
  (:use
   [hiccup.page]))

(defn index-page [request]
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
            :media "screen"}]
    ;;; TODO: move to garden css generation script
    [:style {:type "text/css"}
     "@keyframes dash {
       to {
        stroke-dashoffset: 0;
       }
     }"]
    ]

   [:body
    [:script {:type "text/javascript"
              :src "js/app.js"
              :charset "utf-8"}]]))
