(ns senna.rules
  (:require
   [cljs.core.async :refer [put!]]))

(def ^:const rule-text
  "发挥你的聪明才智，正确答对问题，让小车加速，最快时间到达终点。记住，速度才是王道哦！")

(defn rules-page [chan params l t s]
  [:div#rules.content {:title "游戏规则"}
   [:section rule-text]
   [:button.got-it
    {:href "#"
     :on-click #(put! chan {:next :countdown})}
    "我知道了"]])
