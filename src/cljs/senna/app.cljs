(ns senna.app
  (:require
   [reagent.core :as r]
   [cljs.core.async :as async :refer [>! <! close!]]
   [senna.loader :as loader]
   [senna.game :as game])
  (:require-macros [cljs.core.async.macros :refer [go]]))


;; TODO: use macro to read file name at compile time
(def resources {:round   "img/game/round.svg"
                :round-1 "img/game/1.svg"
                :round-2 "img/game/2.svg"
                :round-3 "img/game/3.svg"

                :loading "img/loading/loading.png"
                :logo "img/loading/CAFlogo.jpg"

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
                :friend-circle "img/social/friend-circle.svg"
                })


(def ^:private dialog (r/atom :rule))

(def ^:private rule-text "发挥你的聪明才智，正确答对问题，让小车加速，最快时间到达终点。记住，速度才是王道哦！")

(defn- rules-page [l t s]
  (let [h (.-innerHeight js/window)
        h1 (* s 1152)
        offset (/ (- h h1) 2)]
    [:div#rules
     [:section {:style {:padding-top (str (+ 130 offset) "px")}}
      [:span rule-text]
      [:div.container
       [:button.got-it {:href "#"
                   :on-click #(reset! dialog :countdown)} ""]]]]))

(def countdown (r/atom 3))

(defn- countdown-page [l t s]
  (if (> @countdown 0)
    (do
      (js/setTimeout #(swap! countdown dec) 1000)
      [:div#countdown
       [:svg.loader
        [:path {:d "M 80,80 m 0,-40
                    a 40,40 0 1 1 0,80
                    a 40,40 0 1 1 0,-80"}]
        ]
       [:div.seconds @countdown]])
    (reset! dialog nil)))

(def ^:private pages {:rule rules-page
                      :countdown countdown-page})


(defn- dialog-component [l t s]
  (if-let [page (pages @dialog)]
    [:div.dialog
     [page l t s]]))

(defn- scene []
  (let [w (.-innerWidth js/window)
        h (.-innerHeight js/window)
        s (/ w 768)
        sh (* 1225 s)
        l -10
        t (-> h
              (- sh)
              (/ 2)
              (/ s))]
    [:div#scene
     [game/score-board]
     [game/game-board l t s]
     [game/game-control l t s]
     [game/ipad-control l t s]
     [dialog-component l t s]]))

(defn init []
  (let [loader (loader/init resources)]
    (go
      _ (<! loader)
      (r/render-component [scene] (.querySelector js/document "body")))))
