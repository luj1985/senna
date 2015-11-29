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

(defn rules-page [chan params l t s]
  [:div#rules.content {:title "游戏规则"}
   [:section rule-text]
   [:button.got-it
    {:href "#"
     :on-click #(put! chan {:next :countdown})}
    "我知道了"]])

(defn countdown-page [chan _ _ _ _]
  (let [s @countdown
        c (circumference inner)
        bg (colors s)
        fg (colors (max (dec s) 0))]

    (if (pos? s)
      (js/setTimeout #(swap! countdown dec) 1000)
      ;; make "Go!" message appears
      (js/setTimeout #(put! chan {:next :start}) 300))

    [:div#countdown
     [:svg.loader
      [:circle {:r radius
                :style {:fill bg}}]
      [:path {:d (path-circle radius radius inner)
              :style {:stroke-dasharray c
                      :stroke-dashoffset c
                      :stroke fg}}]]
     (if (pos? s)
       [:div.txt.seconds s]
       [:div.txt.go "GO!"])]))

(defn result-page [chan params l t s]
  (let [h (.-innerHeight js/window)
        h1 (* s 1152)
        offset (/ (- h h1) 2)
        time (:time params)
        min (js/parseInt (/ time 60))
        secs (js/parseInt (mod time 60))]
    [:div#score.content
     [:section
      [:div.usage
       [:span (str "用时："min "分" secs "秒")]]
      [:div.rank
       [:div.global "全球排名：" (:global params)]
       [:div.best "历史最好：" (:best params)]]
      [:div.container]]]))
