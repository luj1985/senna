(ns senna.dashboard
  (:use [hiccup.page]))

(defn- two-digit-time [t]
  (if (< t 10) (str "0" t) t))

(defn- two-digit-ms [ms]
  (-> ms
      (/ 10)
      (int)
      (two-digit-time)))

(defn- ms->string [ms]
  (let [fsec (int (/ ms 1000))
        min (int (/ fsec 60))
        sec (int (mod fsec 60))
        ms (mod ms 1000)]
    (str min "分"
         (two-digit-time sec) "秒"
         (two-digit-ms  ms))))

(defn- result-section
  [rankings
   {:keys [users-count results-count pages-count start current] :as statistics}]
  [:section
   [:h2 "《小车跑跑跑》用户成绩"]
   [:ol
    [:li (str "总共" users-count "用户提交了" results-count "次成绩") ]
    [:li "有一些用户完成了游戏，但是没有提交电话号码，显示为N/A"]
    [:li "同一用户只取最好成绩"]]
   [:table
    [:thead
     [:tr
      [:th "排名"]
      [:th "手机号"]
      [:th "用时"]
      [:th "用时（毫秒）"]]]
    [:tfoot
     [:tr
      [:td.pagination {:colspan 4}
       (if (> current 1)
         [:a {:href "_dashboard?page=1"} "首页"]
         [:a.disabled {:href "#"} "首页"])
       (if (> current 1)
         [:a {:href (str "_dashboard?page=" (dec current))} "前一页"]
         [:a.disabled {:href "#"} "前一页"])
       [:span.pages (str current " / " pages-count)]
       (if (< current pages-count)
         [:a {:href (str "_dashboard?page=" (inc current))} "后一页"]
         [:a.disabled {:href "#"} "后一页"])
       (if (< current pages-count)
         [:a {:href (str "_dashboard?page=" pages-count)} "尾页"]
         [:a.disabled {:href "#"} "尾页"])
       ]]]
    [:tbody
     (map-indexed (fn [i rank]
                    [:tr
                     [:td (+ start (inc i))]
                     [:td (or (:mobile rank) "N/A") ]
                     [:td (ms->string (:best rank))]
                     [:td (int (:best rank))]]) rankings)]]])

(defn- view-count [views]
  [:section
   [:h2 "品牌点击数统计"]
   [:ol
    [:li "由于网络爬虫的访问，点击数会比实际的高"]]
   [:table
    [:thead
     [:tr
      [:th "品牌"]
      [:th "点击次数"]]]
    [:tbody
     (for [view views]
       [:tr
        [:td (:name view)]
        [:td (:count view)]])]]])



(defn dashboard-page [rankings views totals]
  (html5
   [:head
    [:meta {:charset "utf-8"}]
    [:meta {:http-equiv "x-ua-compatible" :content "ie=edge"}]
    [:meta {:name "renderer" :content "webkit"}]
    (include-css "css/dashboard.css")
    [:title "排行榜"]]
   [:body
    (view-count views)
    (result-section rankings totals)]))

