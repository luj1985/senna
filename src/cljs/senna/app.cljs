(ns senna.app
  (:require
   [reagent.core :as r]
   [cljs.core.async :as async :refer [>! <! close!]]
   [senna.loader :as loader]
   [senna.countdown :as cd]
   [senna.game :as game])
  (:require-macros [cljs.core.async.macros :refer [go]]))

;; TODO: use macro to read file name at compile time
(def resources {:loading "img/loading/loading.jpg"
                :logo "img/loading/CAFlogo.jpg"

                :round   "img/game/round.svg"
                :round-1 "img/game/1.svg"
                :round-2 "img/game/2.svg"
                :round-3 "img/game/3.svg"

                :volume    "img/game/volume.svg"
                :dashboard "img/game/dashboard.png"

                :car "img/game/car.png"
                :track "img/game/track.png"
                :background "img/game/background.jpg"

                :got-it "img/rules/got-it.svg"
                :rules  "img/rules/rules.png"

                :again "img/score/again.svg"
                :close "img/score/close.svg"
                :share "img/score/share.svg"
                :achievement "img/score/achievement.png"

                :qq "img/social/qq.svg"
                :link "img/social/copy-link.svg"
                :weibo "img/social/weibo.svg"
                :wechat "img/social/wechat.svg"
                :qq-space "img/social/qq-space.svg"
                :friend-circle "img/social/friend-circle.svg"})

(defonce ^:private dialog (r/atom :rule))

(defonce ^:private countdown (r/atom 3))

(def ^:private loader-colors ["#F9B72B" "#F39900" "#EA5412" "#E93228"])

(def ^:private rule-text "发挥你的聪明才智，正确答对问题，让小车加速，最快时间到达终点。记住，速度才是王道哦！")

(defn- rules-page [ch l t s]
  (let [h (.-innerHeight js/window)
        h1 (* s 1152)
        offset (/ (- h h1) 2)]
    [:div#rules
     [:section {:style {:padding-top (str (+ 130 offset) "px")}}
      [:span rule-text]
      [:div.container
       [:button.got-it {:href "#"
                        :on-click #(reset! dialog :countdown)} ""]]]]))

(def ^:private pages {:rule rules-page
                      :countdown cd/countdown-page})


(defn- dialog-component [ch l t s]
  (go
    (let [event (<! ch)]
      (case event
        :start (do
                 (game/start)
                 (reset! dialog nil)))))

  (if-let [page (pages @dialog)]
    [:div.dialog
     [page ch l t s]]))

(defn- scene [ch]
  (let [w (.-innerWidth js/window)
        h (.-innerHeight js/window)
        s (/ w 768)
        l -10
        t (-> h (- (* 1225 s)) (/ 2) (/ s))]
    (r/create-class
     {:component-did-mount game/ready
      :reagent-render (fn []
                        [:div#scene
                         [game/score-board]
                         [game/game-board l t s]
                         [game/game-control l t s]
                         [game/ipad-control l t s]
                         [dialog-component ch l t s]])})))

(defn init []
  (let [loader (loader/init resources)
        progress (async/chan)]
    (go
      _ (<! loader)
      (r/render-component [scene progress] (.-body js/document)))))
