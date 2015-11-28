(ns senna.game
  (:require
   [reagent.core :as r]
   [cljs.core.async :as async :refer [>! <! put!]]))

(defonce time-usage (r/atom 0))


;;; TODO: calculate the initial state from the SVG path
(defonce app-state (r/atom {:position {:x 0 :y 0 :r 0}
                            :start-at 0
                            :current-time 0
                            :status :ready
                            :distance 0
                            :rounds 1}))
(defonce speed (r/atom 0))

;;; TODO: read from file ?
(def ^:private track-path "M167,880l-50.194-136.857
  c-16.444-37.68,12.98-66.883,12.98-66.883l69.318-86.978l44.98-50.164
  c29.42-22.987,48.771-7.582,48.771-7.582l77.907,36.716c0,0,47.754,19.771,
  59.17-26.938c9.32-38.141-25.735-49.108-25.735-49.108l-138.543-46.5
  c-70.833-24.576-78.201-60.685-78.201-60.685c-22.063-77.951,66.992-109.914,
  66.992-109.914l197.173-74.105c0,0,49.977-21.582,77.65,6.406c27.675,27.99,
  1.028,49.062,1.028,49.062l-72.764,57.492c0,0-48.134,36.369-28.899,82.838
  c19.236,46.469,66.856,35.594,66.856,35.594c89.073-15.979,72.863-86.914,
  72.863-86.914c-11.03-53.18,33.743-72.936,33.743-72.936l90.141-18.9
  c31.371-5.295,47.659-12.836,48.312-45.195c0.65-32.359-32.713-47.102-32.713-47.102
  L376.851,78.481c0,0-74.88-37.234-114.029-3.12c-22.177,19.325-14.396,
  44.716-14.396,44.716s28.688,49.727-17.452,95.943l-155.5,141.864
  c0,0-31.085,16.547-26.502,109.855l25.224,513.422c0,0-6.671,54.809,55.713,
  73.149c0,0,371.509,116.281,385.061,112.285c65.669-19.358,5.978-121.889,
  100.579-86.871c61.742,22.854,89.18,3.57,76.697-30.018c0,0-14.227-47.354-123.729-27.106
  l-313.541,12.557C254.977,1035.157,208.306,1029.831,199.599,995.858z")

(defn- rotate-along-path [from to]
  (let [x0 (.-x from) y0 (.-y from)
        x1 (.-x to)   y1 (.-y to)]
    (-> (Math/atan2 (- y1 y0) (- x1 x0))
      (* 180)
      (/ Math/PI)
      (+ 90))))


(defn- motion [position]
  (if (< @speed 3)
    (swap! speed #(+ .03 %)))
  (+ position @speed))

(defn- move [p1 p2]
  (let [track (.getElementById js/document "track")
        total (.getTotalLength track)
        from (.getPointAtLength track (mod p1 total))
        to (.getPointAtLength track (mod p2 total))]
    {:x (- (.-x from) 30)
     :y (- (.-y from) 30)
     :r (rotate-along-path from to)}))


(defn ready []
  (swap! app-state assoc :position (move 0 1)))

(defn- timestamp []
  (.getTime (js/Date.)))

(defn- move-forward []
  ;; XXX: for figwheel reloading, sometimes the dom may not ready
  (when-let [track (.getElementById js/document "track")]
    (let [{:keys [status distance]} @app-state]
      (when (= status :running)
        (let [total (.getTotalLength track)
              from distance
              to (motion from)
              rounds (js/Math.ceil (/ to total))
              status (if (<= rounds 3) :running :finished )]
          (swap! app-state assoc
                 :status status
                 :position (move from to)
                 :current-time (timestamp)
                 :distance to
                 :rounds rounds)))))
    (js/requestAnimationFrame move-forward))

(defn start []
  (let [moment (timestamp)]
    (swap! app-state assoc
           :status :running
           :current-time moment
           :start-at moment)
    (move-forward)))

(defn stop []
  (swap! app-state assoc :status :finished))

(defn- car-spirit [l t]
  (let [{:keys [x y r]} (:position @app-state)]
    [:g {:id "car"
         :transform (str "translate(" (+ x l) "," (+ y t) ") rotate(" r ",30,30)")
         :dangerouslySetInnerHTML {:__html "<image xlink:href=\"../img/game/car.png\"
width=\"60\" height=\"60\" x=\"0\" y=\"0\">"}}]))

(defn- track-spirit [l t]
  [:path {:id "track"
          :transform (str "translate(" l "," t ")")
          :d track-path}])

(defn- get-time-usage []
  (let [{:keys [start-at current-time]} @app-state
        used (- current-time start-at)]
    (js/parseInt (/ used 1000))))

(defn game-board [ctrl l t s]
  (let [{status :status} @app-state]
    (when (= status :finished)
      (put! ctrl {:next :finished
                  :params {:time (get-time-usage)
                           :global 1596
                           :best 1000}})))

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

(defn game-control [l t scale]
  (let [{:keys [status start-at current-time]} @app-state
        used (- current-time start-at)
        secs (/ used 1000)
        m (js/parseInt (/ secs 60))
        s (js/parseInt (mod secs 60))
        ms (js/parseInt (/ (mod used 1000) 10))]
    [:div.timer {:style {:zoom scale
                         :top (str (+ 472 t) "px")}}
       [:span.m (to-fixed m)]
       [:span ":"]
       [:span.s (to-fixed s)]
       [:span "."]
       [:span.ms (to-fixed ms)]]))


(defonce ^:private candidates
  (r/atom {:questions []
           :current 0}))

(defn ipad-control [questions l t s]
  (swap! candidates assoc :questions questions)

  (let [{:keys [questions current]} @candidates
        question (get questions current)
        nav-next (fn [e]
                   (.preventDefault e)
                   (swap! candidates assoc :current (inc current)))]
    (if (nil? question)
      [:div.ipad {:style {:zoom s
                          :top (str (+ 622 t) "px")}}
       [:div.message "没有了"]]

      [:div.ipad {:style {:zoom s
                          :top (str (+ 622 t) "px")}}
       [:h4.question (:question question)]
       [:ul.options
        [:li [:a {:href "#" :on-click nav-next} (:option1 question)]]
        [:li [:a {:href "#" :on-click nav-next} (:option2 question)]]
        [:li [:a {:href "#" :on-click nav-next} (:option3 question)]]]])))
