(ns senna.dialog
  (:require
   [cljs.core.async :as async :refer [chan >! <! timeout]]
   [cljs-http.client :as http]
   [reagent.core :refer [atom]]
   [cljs.core.async :refer [put!]]
   [senna.users :as user])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defonce ^:private countdown (atom 3))

(def ^:const colors ["#F9B72B" "#F39900" "#EA5412" "#E93228"])
(def ^:const radius 80)
(def ^:const inner 50)
(def ^:const rule-text
  "发挥你的聪明才智，正确答对问题，让小车加速，最快时间到达终点。记住，速度才是王道哦！")

(def ^:const prize
  "截止到2015年12月5日下午15点整排名前10的玩家，就能赢取惊喜好礼一份！另外，参与游戏就有机会参加抽奖，拼实力，也要拼人品哦！")
(defn- path-circle [rx ry r]
  (str "M" rx "," ry "m" 0 ",-" r
       "a" r "," r ",0,1,1,0," (* 2 r)
       "a" r "," r ",0,1,1,0,-" (* 2 r)))

(defn- circumference [r]
  (* 2 js/Math.PI r))

(defn rules-page [ch _]
  [:div#rules.content {:title "游戏玩法"}
   [:section.important rule-text]
   [:section
    [:p "感谢您参与《小车跑跑跑》法兰克福展活动，根据活动规则，本期共20名玩家获赠礼品，包括10个优胜奖和10个幸运奖。点击“获奖名单”查看获奖详情。近期我们将以电话的方式与中奖玩家联系！尽请关注！"]
    [:p "联系方式：" [:a.tel {:href "tel:021-54731592"} "021-54731592" ]]
    ]
   [:button.black
    {:href "#"
     :on-click #(put! ch {:event :ready})}
    "我知道了"]
   [:a.button.black
    {:href "/winners"} "获奖名单"]])

(defn reset-countdown []
  (reset! countdown 3))

(defn countdown-page [chan _]
  (let [s @countdown
        c (circumference inner)
        bg (colors s)
        fg (colors (max (dec s) 0))]

    [:div#countdown
     [:svg.loader
      [:circle {:r radius
                :style {:fill bg}}]
      [:path {:d (path-circle radius radius inner)
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

(defn- two-digit-millisecond [time]
  (let [milliseconds (mod time 1000)]
    (js/parseInt (/ milliseconds 10))))

(defn- two-digit-number [digit]
  (if (< digit 10) (str "0" digit) digit))

(defn- parse-time [time]
  (let [mins (js/parseInt (/ time 60000))
        mss (-> time
                (mod 1000)
                (/ 10)
                (js/parseInt)
                (two-digit-number))
        secs (-> (/ time 1000)
                 (js/parseInt)
                 (mod 60)
                 (two-digit-number))]
    {:mins mins
     :secs secs
     :mss mss}))

(defonce ^:private last-score (atom {}))

(defn parse-score-response [{:keys [time global best]}]
  (let [{:keys [mins secs mss]} (parse-time time)
        message (str mins "分" secs "秒" mss)]
    {:global global :message message}))

(defn get-last-score []
  @last-score)

(defn result-page [chan params]
  (let [{:keys [time global best]} params
        {:keys [mins secs mss]} (parse-time time)
        message (str mins "分" secs "秒" mss)]
    [:div#score.content
     [:section.score
      [:div.usage
       mins [:span.txt "分"] secs [:span.txt "秒"] mss]
      [:div.rank
       [:div "全球排名：" [:span.history global] "名"]
       [:div "历史最好：" [:span.history
                           (let [{:keys [mins secs mss]} (parse-time best)]
                             (swap! last-score assoc
                                    :global global
                                    :message message )
                             [:span.best
                              mins
                              [:span.txt "分"]
                              secs [:span.txt "秒"]
                              mss])]]]]
     [:div.container
      [:button.black {:on-click #(put! chan {:event :reset})} "再玩一次"]
      [:button.black {:on-click #(put! chan {:event :mobile})} "我要抽奖"]]
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
              :maxlength 11}]
     [:input.black {:id "submit" :type :submit
                    :on-click (fn [e]
                                (let [input (.getElementById js/document "mobile")
                                      value (.-value input)
                                      headers (user/get-uid-headers)]
                                  (if (re-matches #"\d{11}" value)
                                    (go
                                      (let [resp (<! (http/post "/mobile" (merge {:json-params {:number value}} headers) ))]
                                        (put! chan {:event :prize})))
                                    (js/alert "手机号码输入有误"))))
                    :value "确 定"}]]]])


(defn prize-page [chan params]
  [:div.presentation.fullscreen
   [:img.header {:src "img/prize.jpg"}]
   [:h4 "法兰克福展，边玩边拿奖"]
   [:p "截止到2015年12月5日下午15点整排名前10的玩家，就能赢取惊喜好礼一份！"]
   [:p [:em "还有幸运大抽奖，更多好礼送不停！"]]
   [:div.share [:button.black {:on-click #(put! chan {:event :share})} "分 享"]]])
