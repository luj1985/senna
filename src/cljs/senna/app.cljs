(ns senna.app
  (:require [reagent.core :as r]))

;;; TODO: calculate the initial state from the SVG path
(def app-state (r/atom {:transform {:x 57.3749 :y 342.897 :r 0}
                        :distance 0
                        :rounds 0
                        :rotate 0}))

(def ^:private track-path "M194.662,995.858L116.12,746.001
  c-15.906-37.68,12.557-66.883,12.557-66.883l67.053-86.977l43.51-50.164c28.459-22.988,47.178-7.582,47.178-7.582l75.361,36.715
  c0,0,46.194,19.771,57.237-26.938c9.016-38.141-24.895-49.109-24.895-49.109l-134.016-46.5
  c-68.518-24.576-75.646-60.684-75.646-60.684c-21.342-77.951,64.803-109.914,64.803-109.914l190.731-74.105
  c0,0,48.344-21.582,75.113,6.406c26.771,27.99,0.996,49.062,0.996,49.062l-70.387,57.492c0,0-46.561,36.369-27.955,82.838
  c18.607,46.469,64.672,35.594,64.672,35.594c86.162-15.979,70.482-86.914,70.482-86.914c-10.67-53.18,32.641-72.936,32.641-72.936
  l87.195-18.9c30.346-5.295,46.102-12.836,46.732-45.195s-31.643-47.102-31.643-47.102L366.122,78.481
  c0,0-72.434-37.234-110.303-3.12c-21.453,19.325-13.926,44.716-13.926,44.716s27.75,49.727-16.882,95.943L74.592,357.884
  c0,0-30.07,16.547-25.637,109.855l24.4,513.422c0,0-6.453,54.809,53.893,73.15c0,0,359.37,116.281,372.479,112.285
  c63.523-19.359,5.783-121.889,97.293-86.871c59.725,22.854,86.266,3.57,74.191-30.018c0,0-13.762-47.355-119.686-27.107
  l-303.295,12.557C248.231,1035.157,203.084,1029.831,194.662,995.858z")

(defn- rotate-along-path [from to]
  (let [x0 (.-x from) y0 (.-y from)
        x1 (.-x to)   y1 (.-y to)]
    (-> (Math/atan2 (- y1 y0) (- x1 x0))
      (* 180)
      (/ Math/PI)
      (+ 90))))

(def speed (r/atom 0))

(defn- motion [position]
  (if (< @speed 8.5)
    (swap! speed #(+ .2 %)))
  (+ position @speed))

(defn- move-forward [state]
  (let [track (.getElementById js/document "track")
        total (.getTotalLength track)
        old-distance (:distance state)
        new-distance (motion old-distance)
        from (.getPointAtLength track (mod old-distance total))
        to (.getPointAtLength track (mod new-distance total))]
      (assoc state :transform {:x (- (.-x to) 30)
                               :y (- (.-y to) 30)
                               :r (rotate-along-path from to)}
                   :distance new-distance
                   :rounds (js/parseInt (/ new-distance total)))))

(defn car-spirit []
  (fn []
    (js/requestAnimationFrame #(swap! app-state move-forward))
      (let [{:keys [x y r]} (:transform @app-state)]
        [:g {:id "car"
             :transform (str "translate(" x "," y ") rotate(" r ",30,30)")
             :dangerouslySetInnerHTML {
             :__html "<image xlink:href=\"css/car.png\" width=\"60\" height=\"60\" x=\"0\" y=\"0\">"}}])))

(defn track-spirit []
  [:path {:id "track"
          :d track-path}])

(defn- score-board []
  [:div {:id "score-board"}
    [:div.dashboard (str "Speed: " (.toFixed @speed 2)) ]
    [:div.rounds (str "Rounds: " (:rounds @app-state))]])

(defn- game-rules []
  [:div {:id "game-rules"}
    [:p ""]])

(defn game-board []
  [:svg {:id "game-board" :width 780 :height 1221}
    [track-spirit]
    [car-spirit]])

(defn init []
  (r/render-component [game-board] 
    (.getElementById js/document "container"))
  (r/render-component [score-board]
    (.getElementById js/document "statusbar")))