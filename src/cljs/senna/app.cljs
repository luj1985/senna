(ns senna.app
  (:require [reagent.core :as reagent :refer [atom]]))

(def car-size 30)

(def car-position
  (atom {:id "car" :x (- 72.3749 (/ car-size 2)) :y (- 357.897 (/ car-size 2)) :width car-size :height car-size}))

(def offset (atom 0))

(defn- forward [state]
  ;;; TODO: wait dom ready

  (if-let [track (.getElementById js/document "track")]
    (let [total (. track getTotalLength)
          position (. track getPointAtLength @offset)]
      (swap! offset (fn [old]
        (-> old (mod total) (inc))))
      (assoc state :x (- (.-x position) (/ car-size 2))
                   :y (- (.-y position) (/ car-size 2))))
    0))

(defn car-spirit []
  (fn []
    (js/requestAnimationFrame #(swap! car-position forward))
      [:rect @car-position]))

(defn track-spirit []
  [:path {:id "track"
          :d "M72.3749,357.897 C-25.1553,347.934 27.6773,243.027 44.8613,180.541 C63.0055,114.564 108.853,59.8461 158.609,37.2808 C219.166,9.81726 276.447,24.4344 261.373,100.122 C251.411,150.14 157.545,184.703 254.88,229.261 C316.443,257.444 346.572,388.094 254.88,391.516 C177.719,394.395 209.453,272.642 126.416,272.642 C91.7781,272.642 110.768,361.819 72.3749,357.897 Z"}])

(defn game-board []
  [:svg {:width 350 :height 480}
    [track-spirit]
    [car-spirit]])

(defn init []
  (reagent/render-component [game-board]
                            (.getElementById js/document "container")))