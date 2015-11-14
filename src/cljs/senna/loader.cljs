(ns senna.loader
  (:require
    [cljs.core.async :as async :refer [>! <!]]
    [clojure.browser.event :as event :refer [listen-once listen]])
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:import [goog.net ImageLoader EventType]
           [goog.events.EventType]))

(defn preload-images [resources]
  (let [progress (async/chan)
        loader (ImageLoader.)]
    (go
      (listen loader
              goog.events.EventType.LOAD
              #(async/put! progress :load))
      (listen-once loader
                   goog.net.EventType.COMPLETE
                   #(async/put! progress :complete))
      (doseq [[id img] resources]
        (.addImage loader id img))
      (.start loader))
    progress))
