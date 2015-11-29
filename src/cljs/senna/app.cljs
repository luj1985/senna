(ns senna.app
  (:require
   [cljs.core.async :as async :refer [chan >! <!]]
   [reagent.core :as r]
   [senna.loader :as loader]
   [senna.dialog :as dialog]
   [senna.game :as game])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defonce ^:private dialog (r/atom {:dialog :rule}))

;; debug timer
#_(defonce ^:private dialog (r/atom {:dialog :results
                                     :params {:time 0
                                              :global 2000
                                              :best 1000}}))

(def ^:private pages {:rule dialog/rules-page
                      :results dialog/result-page
                      :countdown dialog/countdown-page})

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

(defn init []
  (let [loader (loader/init)
        evbus (async/chan)
        w (.-innerWidth js/window)
        h (.-innerHeight js/window)
        s (/ w 768)
        l -10
        t (-> h (- (* 1225 s)) (/ 2) (/ s))]
    (go
      (let [tasks (<! loader)]
        (r/render-component [game/scene evbus tasks l t s]
                            (.querySelector js/document "#main"))
        (r/render-component [popup evbus l t s]
                            (.querySelector js/document "#dialog"))))))
