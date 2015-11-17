(ns senna.rules
  (:require
   [reagent.core :as r]
   [cljs.core.async :as async :refer [>! <!]])
  (:require-macros [cljs.core.async.macros :refer [go]]))


(def ^:private rule-text "《小车跑跑跑》为竞时问答类游戏，小车行驶过程中，会出现各类关于汽车知识的选择题，
并有A、B、C三个选项，选择你认为正确的答案，如果答对问题，小车会加速；答错或不答，小车会减速，最后在小车抵达终点时用时最短的人获胜。")

(defn rules-page [l t s]
  [:div#rules
   [:div.container
    [:p {:style {:padding-top (str (+ 150 t) "px")}}rule-text]]])
