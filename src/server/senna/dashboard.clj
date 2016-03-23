(ns senna.dashboard
  (:require
   [clojure.pprint :refer [pprint]]
   [hiccup.page :refer :all])
  (:gen-class))

(defn- generate-selector [views]
  (->> views
       (filter :draw)
       (map (fn [v]
              [:option {:value (:id v)} (:name v)]))
       (list* :select {:name "brand"}
              [:option {:value "0" :selected true} "全部"])
       vec))

(defn- result-section
  [rankings
   views
   {:keys [users-count results-count pages-count start current] :as statistics}]
  [:section
   [:h2 "《小车跑跑跑》用户成绩"]
   [:ol
    [:li (str "总共" users-count "用户提交了" results-count "次成绩") ]
    [:li "完成了游戏，但是没有提交电话号码，显示为N/A"]
    [:li "因为没有用户注册，使用设备作为用户标示"]]
   [:div.export
    [:form {:method "POST" :action "/_export"}
     (generate-selector views)
     [:input {:type :submit :value "导出"}]]]
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
                     [:td (:result-str rank)]
                     [:td (int (:result rank))]]) rankings)]]])

(defn- view-count [views]
  [:section
   [:h2 "品牌点击数统计"]
   [:ol
    [:li "由于网络爬虫的访问，点击数会比实际的高"]]
   [:table
    [:thead
     [:tr
      [:th "品牌"]
      [:th "点击次数"]
      [:th "抽奖"]]]
    [:tbody
     (for [view views]
       [:tr
        [:td (:name view)]
        [:td (:count view)]
        [:td
         [:input {:type :checkbox
                  :disabled true
                  :checked (not (nil? (:draw view)))}]]])]]])



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
    (result-section rankings views totals)]))

