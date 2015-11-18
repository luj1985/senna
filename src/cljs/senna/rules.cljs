(ns senna.rules
  (:require
   [reagent.core :as r]
   [cljs.core.async :as async :refer [>! <!]])
  (:require-macros [cljs.core.async.macros :refer [go]]))


(def ^:private rule-text "发挥你的聪明才智，正确答对问题，让小车加速，最快时间到达终点。记住，速度才是王道哦！")

(defn rules-page [l t s]
  (let [h (.-innerHeight js/window)
        h1 (* s 1152)
        offset (/ (- h h1) 2)]
    [:div#rules
     [:div.container
      [:section {:style {:padding-top (str (+ 130 offset) "px")}}
       [:span rule-text]
       [:div.container
        [:a.got-it {:href "#"} " "]]]]]))
