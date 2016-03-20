(ns senna.dialog
  (:require
   [cljs.core.async :as async :refer [chan >! <! timeout put!]]
   [cljs-http.client :as http]
   [reagent.core :refer [atom]]
   [senna.users :as user]
   [senna.utils :refer [parse-time]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn rules-page [ch _]
  [:div#rules.content
   [:div.wrapper
    [:span.rule-head "游戏玩法"]]
   [:section.important
    [:p "发挥你的聪明才智，正确答对问题，让小车加速，最快时间到达终点。"]
    [:p "记住，速度才是王道哦！"]]
   [:button.black
    {:href "#"
     :on-click #(put! ch {:event :ready})}
    "我知道了"]])




(defonce ^:private countdown (atom 3))

(defn- path-circle [o r]
  (str "M" o "," o "m" 0 ",-" r
       "a" r "," r ",0,1,1,0," (* 2 r)
       "a" r "," r ",0,1,1,0,-" (* 2 r)))

(def circumference (partial * 2 js/Math.PI))

(def ^:const colors ["#F9B72B" "#F39900" "#EA5412" "#E93228"])

(defn countdown-page [chan _]
  (let [radius 80
        inner 50
        s @countdown
        c (circumference inner)
        bg (colors s)
        fg (colors (max (dec s) 0))]

    [:div#countdown
     [:svg.loader
      [:circle {:r radius
                :style {:fill bg}}]
      [:path {:d (path-circle radius inner)
              :style {:stroke-dasharray c
                      :stroke-dashoffset c
                      :stroke fg}}]]
     (if (> s 1)
       (do
         (js/setTimeout #(swap! countdown dec) 1000)
         [:div.txt.seconds s])
       (do
         (js/setTimeout #(put! chan {:event :start}) 500)
         [:div.txt.go "GO!"]))]))

(defn reset-countdown []
  (reset! countdown 3))






(defonce ^:private last-score (atom {}))

(defn parse-score-response [{:keys [time global best]}]
  (let [{:keys [mins secs mss]} (parse-time time)
        message (str mins "分" secs "秒" mss)]
    {:global global :message message}))

(defn get-last-score []
  @last-score)

(defn result-page [chan params]
  (let [{:keys [time global best percent]} params
        {:keys [mins secs mss]} (parse-time time)
        message (str mins "分" secs "秒" mss)]
    [:div#score.content
     [:section.score
      [:div.usage
       mins [:span.txt "分"] secs [:span.txt "秒"] mss]
      [:div.rank
       (let [{:keys [mins secs mss]} (parse-time best)]
         (swap! last-score assoc
                :global global
                :message message))
       [:div "您击败了" [:span.history (str percent "%")] "玩家"]
       [:div "全球排名：" [:span.history global] "名"]]]
     [:div.container
      [:button.black {:on-click #(put! chan {:event :reset})} "再玩一次"]]
     [:div.container
      [:a.more {:href "/brands"}
       [:span.left "猛戳这里了解更多"]]]]))






(defn tel-page [chan params]
  [:div#tel.content
   [:section
    [:p "请输入您的手机号码，以便中奖后我们及时与您联系！"]
    [:center
     [:input {:id "mobile"
              :type :tel
              :value (user/get-tel)
              :maxLength 11}]
     [:input.black {:id "submit" :type :submit
                    :on-click (fn [e]
                                (let [input (.getElementById js/document "mobile")
                                      value (.-value input)
                                      headers (user/get-uid-headers)]
                                  (if (re-matches #"\d{11}" value)
                                    (go
                                      (let [resp (<! (http/post "/mobile"
                                                                (merge {:json-params {:number value
                                                                                      :brand (:brand params)}} headers) ))]
                                        (user/set-tel value)
                                        (put! chan {:event :confirm})))
                                    (js/alert "手机号码输入有误"))))
                    :value "确 定"}]]]])



(defn confirm-page [ch params]
  [:div#tel.content
   [:section
    [:p "信息已提交，感谢您的申请！"]
    [:center
     [:button.black {:on-click #(put! ch {:event :close})}
      "返 回"]]]])
