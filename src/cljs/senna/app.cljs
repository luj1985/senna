(ns senna.app
  (:require
   [cljs.core.async :as async :refer [>! <!]]
   [senna.loader :as loader]
   [senna.game :as game])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def resources ["img/logo.jpg" "img/loading.png"])

(defn init []
  (let [progress (loader/preload-images resources)]
    (go
      (while true
        (let [status (<! progress)]
          (if (= status "complete")
            (game/init)))))))
