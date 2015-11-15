(ns senna.loader
  (:require
    [cljs.core.async :as async :refer [>! <!]]
    [clojure.browser.event :as event :refer [listen-once listen]])
  (:import [goog.net ImageLoader EventType]
           [goog.events.EventType]))

(defn preload-images [resources]
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
