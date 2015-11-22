(ns senna.app
  (:require
   [reagent.core :as r]
   [cljs.core.async :as async :refer [chan >! <! close!]]
   [senna.loader :as loader]
   [senna.countdown :as cd]
   [senna.rules :as rules]
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

(def ^:private pages {:rule rules/rules-page
                      :countdown cd/countdown-page})

(defn- popup [ch l t s]
  (go
    (let [event (<! ch)]
      (case event
        :countdown (reset! dialog :countdown)
        :start (do
                 (game/start)
                 (reset! dialog nil)))))

  (if-let [page (pages @dialog)]
    [:div.dimmer
     [page ch l t s]]))

(defn- scene [ch tasks l t s]
  (let [ctrl (chan)]
  (r/create-class
   {:component-did-mount game/ready
    :reagent-render (fn []
                      [:div#scene
                       [game/score-board]
                       [game/game-board l t s]
                       [game/game-control l t s]
                       [game/ipad-control ctrl tasks l t s]])})))

(defn init []
  (let [loader (loader/init resources)
        progress (async/chan)
        w (.-innerWidth js/window)
        h (.-innerHeight js/window)
        s (/ w 768)
        l -10
        t (-> h (- (* 1225 s)) (/ 2) (/ s))]
    (go
      (let [tasks (<! loader)]
        (r/render-component [scene progress tasks l t s]
                            (.querySelector js/document "#main"))
        (r/render-component [popup progress l t s]
                            (.querySelector js/document "#dialog"))))))
