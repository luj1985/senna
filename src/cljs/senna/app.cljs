(ns senna.app
  (:require
   [reagent.core :as r]
   [cljs.core.async :as async :refer [chan >! <! close!]]
   [senna.loader :as loader]
   [senna.countdown :as cd]
   [senna.rules :as rules]
   [senna.results :as result]
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
                :background "img/game/background.png"

                :again "img/score/again.svg"
                :close "img/score/close.svg"
                :share "img/score/share.svg"

                :rules "img/dialog/rules.png"
                :scores "img/dialog/scores.png"
                :achievement "img/dialog/achieve.png"

                :qq "img/social/qq.svg"
                :link "img/social/copy-link.svg"
                :weibo "img/social/weibo.svg"
                :wechat "img/social/wechat.svg"
                :qq-space "img/social/qq-space.svg"
                :friend-circle "img/social/friend-circle.svg"})

(defonce ^:private dialog (r/atom {:dialog :rule}))

;; debug timer
#_(defonce ^:private dialog (r/atom {:dialog :results
                                     :params {:time 29
                                              :global 2000
                                              :best 1000}}))

(def ^:private pages {:rule rules/rules-page
                      :results result/result-page
                      :countdown cd/countdown-page})

(defn- popup [ch l t s]
  (go
    (let [{:keys [next params]} (<! ch)]
      (case next
        :countdown (reset! dialog {:dialog :countdown})
        :finished (reset! dialog {:dialog :results
                                  :params params})
        :start (do
                 (game/start)
                 (reset! dialog nil)))))

  ;; TODO: rename variables ...
  (let [{dialog :dialog params :params} @dialog]
    (if-let [page (pages dialog)]
      [:div.dimmer
       [page ch params l t s]])))

(defn- layout [ch tasks l t s]
  [:div#scene
   [game/score-board]
   [game/game-board ch l t s]
   [game/game-control l t s]
   [game/ipad-control tasks l t s]])

(def ^:private scene
  (with-meta layout
    {:component-did-mount game/ready}))

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
