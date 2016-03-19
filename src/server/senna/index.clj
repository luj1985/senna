(ns senna.index
  (:require
   [clojure.data.json :as json]
   [wechat.core :as w])
  (:use
   [hiccup.page]))

(defn- wx-integration [req]
  (let [sig (w/sign-package req)]
    (str "wx.config("
         (json/write-str {:appId (:appid sig)
                          :timestamp (:timestamp sig)
                          :nonceStr (:noncestr sig)
                          :signature (:signature sig)
                          :jsApiList ["onMenuShareTimeline"
                                      "onMenuSharAppMessage"
                                      "onMenuShareQQ"
                                      "onMenuShareQZone"
                                      "onMenuShareWeibo"]})
         ");")))

(defn index-page [request]
  (html5
   [:head
    [:meta {:charset "utf-8"}]
    [:meta {:name "viewport"
            :content "width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no"}]
    [:meta {:name "renderer" :content "webkit"}]
    [:title "小车跑跑跑"]
    (include-js "//res.wx.qq.com/open/js/jweixin-1.0.0.js")
    [:script (wx-integration request)]
    [:link {:href "css/garden.css"
            :rel "stylesheet"
            :type "text/css"
            :media "screen"}]
    ;; This is just a workaround, seems like auto-prefix in garden doesnt' work.
    ;; Have not time to investigate, just provide the prefixed animation
    [:style {:type "text/css"}
"@-webkit-keyframes countdown {
  to {
    stroke-dashoffset: 0;
  }
}"]]
   [:body
    [:div#main
     ;; This "loading" html fragment will be replaced by React loader component
     ;; after javascript file was loaded.
     ;; Here is used to prevent initial blank page.
     [:div#loading
      [:div.logo]]]
    [:div#dialog]
    [:div#panel]
    (include-js "js/app.js")]))

(defn brands-page [_]
  (html5
   [:head
    [:meta {:http-equiv "content-type" :content "text/html; charset=utf-8"}]
    [:meta {:http-equiv "x-ua-compatible" :content "ie=edge"}]
    [:meta {:name "description" :content ""}]
    [:meta {:name "keywords" :content ""}]
    [:meta {:name "viewport"
            :content "width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no"}]
    [:meta {:name "renderer" :content "webkit"}]
    [:link {:href "/css/garden.css"
            :rel "stylesheet"
            :type "text/css"
            :media "screen"}]
    [:title "CAF中国后市场论坛"]]
   [:body.presentation
    [:img.header {:src "img/header.jpg"}]

    [:table
     [:tr
      [:td [:a {:href "brands/1"} [:img {:src "img/logos/1.jpg"}]]]
      [:td [:a {:href "brands/2"} [:img {:src "img/logos/2.jpg"}]]]
      [:td [:a {:href "brands/3"} [:img {:src "img/logos/3.jpg"}]]]]
     [:tr
      [:td [:a {:href "brands/4"} [:img {:src "img/logos/4.jpg"}]]]
      [:td [:a {:href "brands/5"} [:img {:src "img/logos/5.jpg"}]]]
      [:td [:a {:href "brands/6"} [:img {:src "img/logos/6.jpg"}]]]]
     [:tr
      [:td [:a {:href "brands/7"} [:img {:src "img/logos/7.jpg"}]]]
      [:td [:a {:href "brands/8"} [:img {:src "img/logos/8.jpg"}]]]
      [:td [:a {:href "brands/9"} [:img {:src "img/logos/9.jpg"}]]]]]
    [:div.branding [:img.logo {:src "img/loading/CAFlogo.jpg"}]]
    [:p "由世界顶级汽车供应商、北美最具影响力的汽车后市场供应协会（AASA）创办，致力于中国后市场行业发展，关注后市场行业的人才储备。协会主要职责有：品牌保护、知识产权、中国和地区贸易展、分销渠道、政府关系维护、市场调查、OE部件/应用数据、再制造项目等。"]]))


(defn brand-page [id]
  (html5
   [:head
    [:meta {:http-equiv "content-type" :content "text/html; charset=utf-8"}]
    [:meta {:http-equiv "x-ua-compatible" :content "ie=edge"}]
    [:meta {:http-equiv "pragma" :content "no-cache"}]
    [:meta {:name "description" :content ""}]
    [:meta {:name "keywords" :content ""}]
    [:meta {:name "viewport"
            :content "width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no"}]
    [:meta {:name "renderer" :content "webkit"}]
    [:link {:href "/css/garden.css"
            :rel "stylesheet"
            :type "text/css"
            :media "screen"}]
    [:title "CAF中国后市场论坛"]]
   [:body.presentation
    [:img.header {:src (str "/img/brands/" id ".jpg")}]]))

(defn prize-page [_]
  (html5
   [:head
    [:meta {:http-equiv "content-type" :content "text/html; charset=utf-8"}]
    [:meta {:http-equiv "x-ua-compatible" :content "ie=edge"}]
    [:meta {:name "description" :content ""}]
    [:meta {:name "keywords" :content ""}]
    [:meta {:name "viewport"
            :content "width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no"}]
    [:meta {:name "renderer" :content "webkit"}]
    [:title "法兰克福展"]
    [:link {:href "css/garden.css"
            :rel "stylesheet"
            :type "text/css"
            :media "screen"}]]

   [:body.presentation]))
