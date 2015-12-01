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

;; debug timer
#_(defonce ^:private dialog (r/atom {:dialog :results
                                   :params {:time 0
                                            :global 2000
                                            :best 100200}}))

(defonce app-state (r/atom {:status :initial}))

(def ^:private pages {:rule dialog/rules-page
                      :prize dialog/tel-page
                      :results dialog/result-page
                      :countdown dialog/countdown-page})

(defn- popup [ch]
  (let [{:keys [dialog params]} @dialog]
    (if-let [page (pages dialog)]
      [:div.dimmer
       [page ch params]])))

(defn- draw-scene [ch params]
  (let [w (.-innerWidth js/window)
        h (.-innerHeight js/window)
        s (/ w 768)
        l -10
        t (-> h (- (* 1225 s)) (/ 2) (/ s))]
    (r/render-component [game/scene ch params l t s]
                        (.querySelector js/document "#main"))
    (r/render-component [popup ch]
                        (.querySelector js/document "#dialog"))))

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
