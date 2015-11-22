(ns senna.rules
  (:require
   [reagent.core :refer [atom]]
   [cljs.core.async :refer [put!]]))


(def ^:const rule-text
  "发挥你的聪明才智，正确答对问题，让小车加速，最快时间到达终点。记住，速度才是王道哦！")

(defn- rules-page [ch l t s]
  (let [h (.-innerHeight js/window)
        h1 (* s 1152)
        offset (/ (- h h1) 2)]
    [:div#rules
     [:section {:style {:padding-top (str (+ 130 offset) "px")}}
      rule-text
      [:div.container>button.got-it
       {:href "#"
        :on-click #(put! ch :countdown)}]]]))
