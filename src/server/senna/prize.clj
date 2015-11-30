(ns senna.prize
  (:use [hiccup.page]))

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

   [:body.presentation
    [:img.header {:src "img/prize.jpg"}]
    [:h4 "法兰克福展，边玩边拿奖"]
    [:p "截止到12月5日15:00，游戏排名前10的玩家可赢取价值"
     [:em "200-500元"]
     "的丰厚大奖！"
     [:em "还有幸运大抽奖，更多好礼送不停！"]]
    [:footer.fixed
     "*本轮奖品揭晓时间2015年12月5日"]]))
