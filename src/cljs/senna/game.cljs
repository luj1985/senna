(ns senna.game
  (:require [reagent.core :as r]))

;;; TODO: calculate the initial state from the SVG path
(def app-state (r/atom {:transform {:x 57.3749 :y 342.897 :r 0}
                        :distance 0
                        :rounds 1
                        :rotate 0}))

;;; TODO: read from file ?
(def ^:private track-path "M199.599,995.858l-81.194-249.857
  c-16.444-37.68,12.98-66.883,12.98-66.883l69.318-86.978l44.98-50.164c29.42-22.987,48.771-7.582,48.771-7.582l77.907,36.716
  c0,0,47.754,19.771,59.17-26.938c9.32-38.141-25.735-49.108-25.735-49.108l-138.543-46.5
  c-70.833-24.576-78.201-60.685-78.201-60.685c-22.063-77.951,66.992-109.914,66.992-109.914l197.173-74.105
  c0,0,49.977-21.582,77.65,6.406c27.675,27.99,1.028,49.062,1.028,49.062l-72.764,57.492c0,0-48.134,36.369-28.899,82.838
  c19.236,46.469,66.856,35.594,66.856,35.594c89.073-15.979,72.863-86.914,72.863-86.914c-11.03-53.18,33.743-72.936,33.743-72.936
  l90.141-18.9c31.371-5.295,47.659-12.836,48.312-45.195c0.65-32.359-32.713-47.102-32.713-47.102L376.851,78.481
  c0,0-74.88-37.234-114.029-3.12c-22.177,19.325-14.396,44.716-14.396,44.716s28.688,49.727-17.452,95.943l-155.5,141.864
  c0,0-31.085,16.547-26.502,109.855l25.224,513.422c0,0-6.671,54.809,55.713,73.149c0,0,371.509,116.281,385.061,112.285
  c65.669-19.358,5.978-121.889,100.579-86.871c61.742,22.854,89.18,3.57,76.697-30.018c0,0-14.227-47.354-123.729-27.106
  l-313.541,12.557C254.977,1035.157,208.306,1029.831,199.599,995.858z")

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
                   :rounds (.ceil js/Math (/ new-distance total)))))

(defn- car-spirit [l t]
  (fn []
    (js/requestAnimationFrame #(swap! app-state move-forward))
      (let [{:keys [x y r]} (:transform @app-state)]
        [:g {:id "car"
             :transform (str "translate(" (+ x l) "," (+ y t) ") rotate(" r ",30,30)")
             :dangerouslySetInnerHTML {
             :__html "<image xlink:href=\"../img/game/car.png\"
width=\"60\" height=\"60\" x=\"0\" y=\"0\">"}}])))

(defn- track-spirit [l t]
  [:path {:id "track"
          :transform (str "translate(" l "," t ")")
          :d track-path}])

(defn game-board [l t s]
  [:div.main
    ;;; The size of SVG path is smaller than its viewbox
   [:svg {:id "game-board"
          :width "100%"
          :style {:zoom s}
          :height "100%"}
    [track-spirit l t]
    [car-spirit l t]]])

(defn- range-map [[d1 d2 :as domain] [r1 r2 :as range]]
  (let [step (/ (- r2 r1) (- d2 d1))]
    (fn [d]
      (cond
        (> d d2) r2
        (< d d1) r1
        :else (+ r1 (* d step))))))

(defn- speed-dashboard []
  (let [f (range-map [0 12] [-110 110])
        deg (f @speed)]
    [:div.dashboard
     [:span.pointer {:style {:transform (str "rotate(" deg "deg)")}}]]))

(defn- round-counter [n]
  (if (#{1,2,3} n)
    (str "url(../img/game/" n ".svg)")
    "url(../img/game/3.svg)"))

(defn- round-dashboard []
  (let [img (round-counter (:rounds @app-state))]
    [:div.rounds
     [:span.prefix]
     [:span.round {:style {:background-image img}}]]))

(defn- volume-control []
  [:div.volume])

(defn score-board []
  [:div.score-board
   [speed-dashboard]
   [round-dashboard]
   [volume-control]])

(defn- to-fixed [n]
  (if (< n 10)
    (str "0" n)
    (str n)))

(defn- to-time [n]
  (let [mins (js/parseInt (/ n 60))
        secs (mod n 60)]
    (str (to-fixed mins) ":" (to-fixed secs))))


(def time-usage (r/atom 0))

;;; TODO: move to game start control logical
(js/setInterval #(swap! time-usage inc) 1000)


(defn ipad-control [l t s]
  [:div.ipad {:style {:zoom s
                      :top (str (+ 622 t) "px")
                      :left (str (+ 188 t) "px")}}])


(defn game-control [l t s]
  [:div.timer {:style {:zoom s
                       :font-size "60px"
                       :height "80px"
                       :width "148px"
                       :top (str (+ 470 t) "px")
                       :left (str (+ 572 l) "px")}}
   (to-time @time-usage) ])
