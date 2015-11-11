(ns senna.app
  (:require [reagent.core :as r]))

(def ^:private CAR-SIZE 30)

(def app-state (r/atom {:position {:x 57.3749 :y 342.897}
                      :distance 0}))

(defn- move-forward [state]
  ;;; TODO: wait dom ready
  (let [track (.getElementById js/document "track")
        total (.getTotalLength track)
        move-to (-> (:distance state) (mod total) (inc))
        position (.getPointAtLength track move-to)]
      (assoc state :position {:x (- (.-x position) 15)
                              :y (- (.-y position) 15)}
                   :distance move-to)))

(defn car-spirit []
  (fn []
    (js/requestAnimationFrame #(swap! app-state move-forward))
      [:rect (merge {:id "car" :width CAR-SIZE :height CAR-SIZE} (:position @app-state))]))

(defn track-spirit []
  [:path {:id "track"
          :d "M72.3749,357.897 C-25.1553,347.934 27.6773,243.027 44.8613,180.541 
              C63.0055,114.564 108.853,59.8461 158.609,37.2808 
              C219.166,9.81726 276.447,24.4344 261.373,100.122 
              C251.411,150.14 157.545,184.703 254.88,229.261 
              C316.443,257.444 346.572,388.094 254.88,391.516 
              C177.719,394.395 209.453,272.642 126.416,272.642 
              C91.7781,272.642 110.768,361.819 72.3749,357.897 Z"}])

(defn game-board []
  [:svg {:width 350 :height 480}
    [track-spirit]
    [car-spirit]])

(defn init []
  (r/render-component [game-board]
                            (.getElementById js/document "container")))