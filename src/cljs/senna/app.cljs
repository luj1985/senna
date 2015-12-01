(ns senna.app
  (:require
   [cljs.core.async :as async :refer [chan >! <! timeout]]
   [cljs-http.client :as http]
   [reagent.core :as r]
   [senna.loader :as loader]
   [senna.dialog :as dialog]
   [senna.game :as game]
   [senna.sound :as sound])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defonce ^:private dialog (r/atom {:dialog :rule}))
(defonce ^:private sharing (r/atom false))

;; debug timer
#_(defonce ^:private dialog (r/atom {:dialog :results
                                     :params {:time 0
                                              :global 2000
                                              :best 100200}}))

(defonce app-state (r/atom {:status :initial}))

(def ^:private pages {:rule dialog/rules-page
                      :mobile dialog/tel-page
                      :prize dialog/prize-page
                      :results dialog/result-page
                      :countdown dialog/countdown-page})

(defn- popup [ch]
  (let [{:keys [dialog params]} @dialog]
    (if-let [page (pages dialog)]
      [:div.dimmer
       [page ch params]])))

(defn social-component [chan params]
  (if @sharing
    [:div.dimmer
     [:div#social
      [:h4 "分享至"]
      [:table
       [:tr
        [:td
         [:a.share {:href "#"}
          [:img {:src "img/social/wechat.svg"}]
          [:h5 "微信"]]]
        [:td
         [:a.share {:href "#"}
          [:img {:src "img/social/friend-circle.svg"}]
          [:h5 "微信朋友圈"]]]
        [:td
         [:a.share {:href "#"}
          [:img {:src "img/social/qq.svg"}]
          [:h5 "QQ"]]]]
       [:tr
        [:td
         [:a.share {:href "#"}
          [:img {:src "img/social/qzone.svg"}]
          [:h5 "QQ空间"]]]
        [:td
         [:a.share {:href "#"}
          [:img {:src "img/social/weibo.svg"}]
          [:h5 "微博"]]]
        [:td
         [:a.share {:href "#"}
          [:img {:src "img/social/copy-link.svg"}]
          [:h5 "复制链接地址"]]]]]
      [:button.cancel {:on-click #(reset! sharing false)} "取 消"]]]))

(defn- draw-scene [ch params]
  (let [w (.-innerWidth js/window)
        h (.-innerHeight js/window)
        s (/ w 768)
        l -10
        t (-> h (- (* 1225 s)) (/ 2) (/ s))]
    (r/render-component [game/scene ch params l t s]
                        (.querySelector js/document "#main"))
    (r/render-component [popup ch]
                        (.querySelector js/document "#dialog"))
    (r/render-component [social-component ch]
                        (.querySelector js/document "#panel"))))

(defn- save-score [{time :time}]
  (go
    (let [resp (<! (http/post "/score" {:json-params {:score time}}))
          model (assoc (:body resp) :time time)]
      (reset! dialog {:dialog :results :params model}))))

(defn init []
  (let [ch (async/chan)]
    (go (while true
          (let [{:keys [event params]} (<! ch)]
            (case event
              ;; display game board when all resources loaded
              :loaded (draw-scene ch (shuffle params) )
              :ready (reset! dialog {:dialog :countdown})
              :share (reset! sharing true)
              :mobile (reset! dialog {:dialog :mobile})
              :prize (reset! dialog {:dialog :prize})
              :reset (do
                       (game/reset)
                       (dialog/reset-countdown)
                       (reset! dialog {:dialog :countdown}))
              :start (do
                       (game/start)
                       (reset! dialog nil))
              :finished (save-score params)
              (js/console.warn "unhandled event:" event)))))
    (loader/init ch)))
