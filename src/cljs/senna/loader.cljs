(ns senna.loader
  (:require 
    [cljs.core.async :as async]
    [clojure.browser.event :as event])
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:import [goog.net ImageLoader]))

(def resources ["img/logo.jpg" "img/loading.png"])

(defn prload []
  (let [loader (ImageLoader.)
        complete (async/chan)]
    (go
      (event/listen-once loader "complete" #(async/put! complete loader))
      (doseq [img resources]
        (.addImage loader img img))
      (.start loader)
      (<! complete))))

(defn init []
  (prload))