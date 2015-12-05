(ns senna.dashboard
  (:use [hiccup.page]))

(defn- two-digit-ms [ms]
  (int (/ ms 10)))

(defn- two-digit-time [t]
  (if (< t 10) (str "0" t) t))

(defn- ms->string [ms]
  (let [fsec (int (/ ms 1000))
        min (int (/ fsec 60))
        sec (int (mod fsec 60))
        ms (mod ms 1000)]
    (str min "分"
         (two-digit-time sec) "秒"
         (two-digit-ms  ms))))

(defn dashboard-page [rankings]
  (html5
   [:head
    [:meta {:http-equiv "content-type" :content "text/html; charset=utf-8"}]
    [:meta {:http-equiv "x-ua-compatible" :content "ie=edge"}]
    [:meta {:name "description" :content ""}]
    [:meta {:name "keywords" :content ""}]
    [:meta {:name "renderer" :content "webkit"}]
    [:style "table { width: 400px; text-align: right;}"]
    [:title "排行榜"]]

   [:body
    [:section
     [:h2 "《小车跑跑跑》用户成绩"]
     [:ol
      [:li "有一些用户完成了游戏，但是没有提交电话号码，用N/A来代替。"]
      [:li "同一用户只取最好成绩"]]

     [:table
      [:thead
       [:tr
        [:th "排名"]
        [:th "手机号"]
        [:th "用时"]
        [:th "用时（毫秒）"]]]
      [:tbody
       (map-indexed (fn [i rank]
                      [:tr
                       [:td (inc i)]
                       [:td (or (:mobile rank) "N/A") ]
                       [:td (ms->string (:best rank))]
                       [:td (int (:best rank))]]) rankings)]]]]))
