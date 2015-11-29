(ns senna.loader
  (:require
   [reagent.core :as r]
   [cljs-http.client :as http]
   [cljs.core.async :as async :refer [>! <! chan]]
   [clojure.browser.event :as event :refer [listen-once listen]])
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:import [goog.net ImageLoader EventType]
           [goog.events.EventType]))

(defonce ^:private loading-state (r/atom {:total 100
                                          :progress 1}))

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
(defn- loading-page []
  [:div#loading
   [:div.logo]
   [:div.progress-bar
    [:progress {:max (:total @loading-state)
                :value (:progress @loading-state)}]]])

(defn- preload-images [resources]
  (let [progress (async/chan)
        loader (ImageLoader.)]
    ;; The callback function is running async, cannot put them into
    ;; one 'go' block
    (listen loader
            goog.events.EventType.LOAD
            #(async/put! progress :load))
    (listen-once loader
                 goog.net.EventType.COMPLETE
                 #(async/put! progress :complete))
    (doseq [[id img] resources]
      (.addImage loader id img))
    (.start loader)
    progress))
(defonce myqs (r/atom nil))


(defn init []
  ;; one additional resource is for questions loading
  (swap! loading-state assoc
         :total (inc (count resources))
         :progress 1)
  (r/render-component [loading-page] (.querySelector js/document "#main"))
  (let [progress (preload-images resources)
        qch (http/get "/questions")
        ch (async/chan)]
    (go
      (while true
        (case (<! progress)
          :load (swap! loading-state update-in [:progress] inc)
          :complete (let [{questions :body} (<! qch)]
                      (>! ch questions)))))

    ch))
