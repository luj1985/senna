(ns senna.app
  (:require
   [cljs.core.async :as async :refer [chan >! <! timeout put!]]
   [cljs-http.client :as http]
   [reagent.core :as r]
   [senna.users :as user]
   [senna.loader :as loader]
   [senna.dialog :as dialog]
   [senna.game :as game]
   [senna.sound :as sound])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defonce ^:private dialog (r/atom nil))
(defonce ^:private sharing (r/atom false))

;; debug timer
#_(defonce ^:private dialog (r/atom {:dialog :results
                                     :params {:time 0
                                              :global 2000
                                              :best 100200}}))

(defonce app-state (r/atom {:status :initial}))

(def ^:private pages {:rule dialog/rules-page
                      :mobile dialog/tel-page
                      :results dialog/result-page
                      :confirm dialog/confirm-page
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

(defn- share-qq [e]
  (.preventDefault e)
  (let [{:keys [global message]} (dialog/get-last-score)
        link (get-game-link)
        qq-message (str  "我在《小车跑跑跑》游戏中用时" message "，全球排名" global "名。处女座，可敢一战？")
        url (str "http://connect.qq.com/widget/shareqq/index.html?"
                 "url=" (js/encodeURIComponent link)
                 "&desc=" (js/encodeURIComponent qq-message)
                 "&title=" (js/encodeURIComponent "#逼死处女座#（来自@CAF汽车后市场论坛）")
                 "&summary=" (js/encodeURIComponent "看一款游戏怎样逼死处女座！"))]
    (set! (.-href js/location) url)))

(defn- share-qzone [e]
  (.preventDefault e)
  (let [{:keys [global message]} (dialog/get-last-score)
        link (get-game-link)
        title "#逼死处女座#（来自@CAF汽车后市场论坛）"
        qzone-message (str  "我在《小车跑跑跑》游戏中用时" message "，全球排名" global "名。处女座，可敢一战？")
        url (str "http://sns.qzone.qq.com/cgi-bin/qzshare/cgi_qzshare_onekey?"
                 "url=" (js/encodeURIComponent link)
                 "&title=" (js/encodeURIComponent title)
                 "&summary=" (js/encodeURIComponent qzone-message))]
    (set! (.-href js/location) url)))


(def home-page "http://caf.shangtao-china.com")
(def logo-image "http://caf.shangtao-china.com/img/loading/CAFlogo.jpg")

(defn- initialize-result-sharing [params]
  (when-let [wx (.-wx js/window)]
    (let [{:keys [global message]} (dialog/parse-score-response params)
          title "#逼死处女座#（来自@CAF汽车后市场论坛）"
          desc (str  "我在《小车跑跑跑》游戏中用时" message "，全球排名" global "名。处女座，可敢一战？")]

      (.onMenuShareTimeline js/window.wx (clj->js {:title desc :link home-page :imgUrl logo-image}))
      (.onMenuShareAppMessage js/window.wx (clj->js {:title title :desc desc :link home-page :imgUrl logo-image}))
      (.onMenuShareQQ js/window.wx (clj->js {:title title :desc desc :link home-page :imgUrl logo-image}))
      (.onMenuShareWeibo js/window.wx (clj->js {:title title :desc desc :link home-page :imgUrl logo-image}))
      (.onMenuShareQZone js/window.wx (clj->js {:title title :desc desc :link home-page :imgUrl logo-image}))

      (set! (.-title js/document) desc))))

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
         [:a.share {:href "#" :on-click share-qq}
          [:img {:src "img/social/qq.png"}]
          [:h5 "QQ"]]]
        [:td [:a.share {:href "#" :on-click share-qzone}
              [:img {:src "img/social/qzone.png"}]
              [:h5 "QQ空间"]]]
        [:td
         [:a.share {:href "#" :on-click share-weibo}
          [:img {:src "img/social/weibo.png"}]
          [:h5 "微博"]]]]
       [:tr
        [:td
         [:a.share {:href "#" :on-click copy-link}
          [:img {:src "img/social/copy-link.png"}]
          [:h5 "复制链接地址"]]]
        [:td]
        [:td]]]
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
  (let [headers (user/get-uid-headers)]
    (go
      (let [resp (<! (http/post "/score" (merge {:json-params {:score time}} headers) ))
            model (assoc (:body resp) :time time)]
        (reset! dialog {:dialog :results :params model})
        (initialize-result-sharing model)))))

(defn- ignore-rule-page? []
  (pos? (.indexOf js/location.search "ignore")))

(defn- initialize-game-scene [ch params]
  (draw-scene ch (shuffle params))
  (if (ignore-rule-page?)
    (put! ch {:event :ready})))

(defn- attach-event-handlers [ch]
  (go
    (while true
      (let [{:keys [event params]} (<! ch)]
        (case event
          ;; display game board when all resources loaded
          :loaded (initialize-game-scene ch params)
          :ready (reset! dialog {:dialog :countdown})
          :share (reset! sharing true)
          :mobile (reset! dialog {:dialog :mobile
                                  :params params})
          :reset (do
                   (game/reset)
                   (dialog/reset-countdown)
                   (reset! dialog {:dialog :countdown}))
          :confirm (reset! dialog {:dialog :confirm})
          :close (reset! dialog nil)
          :start (do
                   (game/start)
                   (reset! dialog nil))
          :finished (save-score params)
          (js/console.warn "unhandled event:" event))))))

(defn- init-game []
  (let [ch (chan)]
    (reset! dialog {:dialog :rule})
    (attach-event-handlers ch)
    (loader/init ch)))

(defn- mobile-for-draw-dialog [id ch params]
  [:button.red
   {:on-click #(put! ch {:event :mobile
                         :params {:brand id}})}
   "申请抽奖" ])

(defn- init-brand-page [id]
  (let [ch (chan)]
    (attach-event-handlers ch)
    (r/render-component [mobile-for-draw-dialog id ch]
                        (.querySelector js/document "#actions"))
    (r/render-component [popup ch]
                        (.querySelector js/document "#dialog"))))

(defn- pick-brand-id [path]
  (let [matches (re-find #"/brands/(\d+)" path)]
    (js/parseInt (get matches 1))))

(defn init []
  (let [path (-> js/document
                 .-location
                 .-pathname)]
    (cond
      (= path "/") (init-game)
      (.match path #"/brands/\d+") (init-brand-page (pick-brand-id path))
      :else (js/alert (str "Unknow page: " path)))))
