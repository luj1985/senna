(ns senna.loader
  (:require
    [reagent.core :as r]
    [cljs.core.async :as async :refer [>! <!]]
    [clojure.browser.event :as event :refer [listen-once listen]])
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:import [goog.net ImageLoader EventType]
           [goog.events.EventType]))

(defonce ^:private loading-state (r/atom {:total 0
                                          :progress 0}))

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


(defn init [resources]
  (swap! loading-state assoc :total (count resources))
  (r/render-component [loading-page] (.querySelector js/document "body"))
  (let [progress (preload-images resources)
        ch (async/chan)]
    (go
      (while true
        (case (<! progress)
          :load (swap! loading-state update-in [:progress] inc)
          :complete (>! ch :complete))))
    ch))
