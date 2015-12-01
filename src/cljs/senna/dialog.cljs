(ns senna.dialog
  (:require
   [reagent.core :refer [atom]]
   [cljs.core.async :refer [put!]]))

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
   [:section prize]
   [:button.black
    {:href "#"
     :on-click #(put! ch {:event :ready})}
    "我知道了"]])

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

(defn result-page [chan params]
  (let [{:keys [time global best]} params
        {:keys [mins secs mss]} (parse-time time)]
    [:div#score.content
     [:section.score
      [:div.usage
       mins [:span.txt "分"] secs [:span.txt "秒"] mss]
      [:div.rank
       [:div "全球排名：" [:span.history global] "名"]
       [:div "历史最好：" [:span.history
                           (let [{:keys [mins secs mss]} (parse-time best)]
                             [:span.best
                              mins
                              [:span.txt "分"]
                              secs [:span.txt "秒"]
                              mss])]]]]
     [:div.container
      [:button.black {:on-click #(put! chan {:event :reset})} "再玩一次"]
      [:button.black {:on-click #(put! chan {:event :prize})} "我要抽奖"]]
     [:div.container
      [:a.more {:href "/brands"}
       [:span.left "猛戳这里了解更多"]]]]))


(defn tel-page [chan params]
  [:div#tel.content
   [:section
    [:p "请输入您的手机号码，以便中奖后我们及时与您联系！"]
    [:center
     [:form {:method :post :action "/mobile"}
      [:input {:id "mobile" :name "mobile" :type :tel}]
      [:input.black {:id "submit" :type :submit :required true
                     :on-submit (fn [e]
                                  (.preventDefault e)
                                  (js/console.log "before submit"))
                      :value "确定"}]]]]])
