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

(defn- path-circle [rx ry r]
  (str "M" rx "," ry "m" 0 ",-" r
       "a" r "," r ",0,1,1,0," (* 2 r)
       "a" r "," r ",0,1,1,0,-" (* 2 r)))

(defn- circumference [r]
  (* 2 js/Math.PI r))

(defn rules-page [ch _]
  [:div#rules.content {:title "游戏玩法"}
   [:section rule-text]
   [:button
    {:href "#"
     :on-click #(put! ch {:event :ready})}
    "我知道了"]])

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

(defn- parse-time [time]
  (let [mins (js/parseInt (/ time 60000))
        secs (-> (/ time 1000)
                 (js/parseInt)
                 (mod 60))
        millis (two-digit-millisecond time)]
    {:mins mins
     :secs (str secs "." millis) }))

(defn result-page [chan params]
  (let [{:keys [time global best]} params
        {:keys [mins secs]} (parse-time time)]
    [:div#score.content
     [:section
      [:div.usage
       [:span (str "用时："mins "分" secs "秒")]]
      [:div.rank
       [:div.global "全球排名：" global]
       [:div.best "历史最好：" best]]]]))
