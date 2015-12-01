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


(defn- get-game-link []
  (.-href js/location))

(defn- copy-link [e]
  (let [href (get-game-link)
        target (.querySelector js/document "#social-link")
        range (.createRange js/document)]

    (.preventDefault e)

    (set! (.-innerText target) href)
    (.selectNode range target)
    (-> js/window
        (.getSelection)
        (.addRange range))
    (.execCommand js/document  "copy")
    (-> js/window
        (.getSelection)
        (.removeAllRanges))))

(def ^:private weibo-message
  )

(defn- share-weibo [e]
  (.preventDefault e)
  (let [{:keys [global message]} (dialog/get-last-score)
        weibo-message (str  "#逼死处女座#我在《小车跑跑跑》游戏中用时" message "，全球排名" global "名。处女座，可敢一战？（来自@CAF汽车后市场论坛）")
        title (js/encodeURIComponent weibo-message)
        link (js/encodeURIComponent (get-game-link))
        url (str "http://service.weibo.com/share/share.php?"
                 "title=" title "&url=" link)]
    (set! (.-href js/location) url)))

(defn social-component [chan params]
  (if @sharing
    [:div.dimmer
     [:span#social-link]
     [:div#social
      [:h4 "分享至"]
      [:table
       [:tr
        [:td
         [:a.share {:href "#"}
          [:img {:src "img/social/wechat.png"}]
          [:h5 "微信"]]]
        [:td
         [:a.share {:href "#"}
          [:img {:src "img/social/friend-circle.png"}]
          [:h5 "微信朋友圈"]]]
        [:td
         [:a.share {:href "#"}
          [:img {:src "img/social/qq.png"}]
          [:h5 "QQ"]]]]
       [:tr
        [:td
         [:a.share {:href "#"}
          [:img {:src "img/social/qzone.png"}]
          [:h5 "QQ空间"]]]
        [:td
         [:a.share {:href "#" :on-click share-weibo}
          [:img {:src "img/social/weibo.png"}]
          [:h5 "微博"]]]
        [:td
         [:a.share {:href "#" :on-click copy-link}
          [:img {:src "img/social/copy-link.png"}]
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
