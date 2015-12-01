(ns senna.game
  (:require
   [reagent.core :as r]
   [senna.sound :as sound]
   [cljs.core.async :as async :refer [>! <! put! chan timeout]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(enable-console-print!)

(declare caculate-speed)

(def ^:const MAX-ROUNDS 3)
(def ^:const FPS 30)
;; Used to indidate the speed dashboard
(def ^:const SPEED-LIMIT 15)
(def ^:const SPEED-NORMAL 5)

(def ^:const ACC-TIME 70)
(def ^:const ACC-UP 0.2)
(def ^:const ACC-DECAY 2)
(def ^:const ACC-DOWN (- (/ ACC-UP ACC-DECAY)))

(defonce game-state (r/atom {:status :ready
                             :position {:x 0 :y 0 :r 0}
                             :distance 0
                             :rounds 1
                             :speed 0
                             :start-at 0
                             :current-time 0}))

(defonce accelerators (r/atom []))

(defn- timestamp []
  (.getTime (js/Date.)))

(defn- rotate-along-path [from to]
  (let [x0 (.-x from) y0 (.-y from)
        x1 (.-x to)   y1 (.-y to)]
    (-> (Math/atan2 (- y1 y0) (- x1 x0))
      (* 180)
      (/ Math/PI)
      (+ 90))))

(defn- move-along [track p1 p2 total]
  (let [from (.getPointAtLength track (mod p1 total))
        to (.getPointAtLength track (mod p2 total))]
    {:x (- (.-x from) 30)
     :y (- (.-y from) 30)
     :r (rotate-along-path from to)}))

(defn- initialize-game-board
  "Used to set initial state of the game, update the car position.
   At this time, the game is not started yet."
  []
  (let [track (.getElementById js/document "track")
        total (.getTotalLength track)]
    (swap! game-state assoc
           :track track
           :total total
           :position (move-along track 0 1 total))))


(defmulti transit :status)

(defmethod transit :running [state]
  (let [{:keys [total distance track speed]} state
        ;; TODO: speed can change at any time
        new-speed (caculate-speed speed)
        to (+ distance (min new-speed SPEED-LIMIT))
        position (move-along track distance to total)
        rounds (js/Math.ceil (/ to total))]
    (assoc state
           :speed new-speed
           :status (if (<= rounds MAX-ROUNDS) :running :finished )
           :position position
           :current-time (timestamp)
           :distance to
           :rounds rounds)))

(defmethod transit :default [state]
  state)

(defn- game-loop []
  (swap! game-state transit)

  (if (= (:status @game-state) :running)
    ;; 'requestAnimationFrame' will pause when stay in background.
    ;; And device may have different frame-rate, use setTimeout instead
    ;; http://creativejs.com/resources/requestanimationframe/
    (js/setTimeout game-loop (/ 1000 FPS))))

(defn reset []
  (swap! game-state assoc
         :status :ready
         :distance 0
         :start-at 0
         :current-time 0
         :speed 0
         :rounds 1)
  (reset! accelerators [])
  (initialize-game-board))

(defn start []
  (let [moment (timestamp)]
    (swap! game-state assoc
           :status :running
           :current-time moment
           :start-at moment)
    (game-loop)))

(def ^:private car-img
  "<image xlink:href=\"../img/game/car.png\" width=\"60\" height=\"60\" x=\"0\" y=\"0\">")

(defn- car-spirit [l t]
  (let [{:keys [x y r]} (:position @game-state)]
    [:g {:id "car"
         :transform (str "translate(" (+ x l) "," (+ y t) ") rotate(" r ",30,30)")
         :dangerouslySetInnerHTML {:__html car-img}}]))

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

(defn- track-spirit [l t]
  [:path {:id "track"
          :transform (str "translate(" l "," t ")")
          :d track-path}])

(defn- get-time-usage []
  (let [{:keys [start-at current-time]} @game-state]
    (- current-time start-at)))

(defn- game-board [ctrl l t s]
  (let [{status :status} @game-state]
    (when (= status :finished)
      (put! ctrl {:event :finished
                  :params {:time (get-time-usage)
                           :global 1596
                           :best 1000}})))

  [:div.main
    ;;; The size of SVG path is smaller than its viewbox
   [:svg {:width "100%"
          :height "100%"
          :style {:zoom s}}
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
  (let [f (range-map [0 SPEED-LIMIT] [-110 110])
        speed (:speed @game-state)
        deg (f speed)]
    [:div.dashboard
     [:span.pointer {:style {:-webkit-transform (str "rotate(" deg "deg)")
                             :transform (str "rotate(" deg "deg)")}}]]))

(defn- round-counter [n]
  (if (#{1,2,3} n)
    (str "url(../img/game/" n ".svg)")
    "url(../img/game/3.svg)"))

(defn- round-dashboard []
  (let [img (round-counter (:rounds @game-state))]
    [:div.rounds
     [:span.prefix]
     [:span.round {:style {:background-image img}}]]))

(defn- score-board []
  [:div.score-board
   [speed-dashboard]
   [round-dashboard]
   [sound/volume-control]])

(defn- to-fixed [n]
  (if (< n 10)
    (str "0" n)
    (str n)))

(defn- game-control [l t scale]
  (let [{:keys [status start-at current-time]} @game-state
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
           :current 0
           :status :normal}))




(defn- normal-speed [speed]
  (if (< speed SPEED-NORMAL) (+ speed 0.05) speed))

#_(defn- debug-log [obj]
  (js/console.log (.stringify js/JSON (clj->js obj)) ))

(defn- accelerate-speed [speed]
  (let [results (->> @accelerators
                     (map (fn [{:keys [remain delta]}]
                            (let [d (cond
                                      (and (< (* ACC-DECAY ACC-TIME) remain)
                                           (<= remain (* (inc ACC-DECAY) ACC-TIME))) ACC-UP
                                      (<= 0 remain (* ACC-DECAY ACC-TIME)) ACC-DOWN
                                      :else 0)]
                              {:remain (dec remain) :delta d}))))
        delta (->> (map :delta results) (reduce +))
        remains (filter #(pos? (:remain %)) results)]
    (reset! accelerators remains)
    (+ speed delta)))

(defn- caculate-speed [speed]
  (-> speed
      (normal-speed)
      (accelerate-speed)))

(defn- speedup-effect [ch correct?]
  (when correct?
    (swap! accelerators conj {:remain (* 3 ACC-TIME) :delta 0})))

(defn- navigate-next [ch correct?]
  (let [status (if correct? :correct :wrong)
        delay (if correct? 500 1000)]
    (go
      (swap! candidates assoc :status status)
      (<! (timeout delay))
      (swap! candidates assoc :status :normal)
      (let [{:keys [total current questions]} @candidates]
        (if (>= (inc current) total)
          (swap! candidates assoc
                 :questions (shuffle questions)
                 :current 0)
          (swap! candidates update-in [:current] inc))))))

(defn- answer-effect [responser correct?]
  (let [sound (if correct? "m-correct" "m-wrong")]
    (sound/play-sound sound))
  (speedup-effect responser correct?)
  (navigate-next responser correct?))

(defn- ipad-control [questions l t s]
  (swap! candidates assoc
         :questions questions
         :total (count questions))

  (let [responser (async/chan)
        {:keys [questions current status]} @candidates
        {:keys [question option1 option2 option3 answer]} (get questions current)
        choice-handler #(fn [e]
                          (.preventDefault e)
                          ;; to prevent non-sense click
                          (if (= (:status @candidates) :normal)
                            (answer-effect responser (= answer %))))]

    [:div.ipad {:style {:zoom s :top (str (+ 625 t) "px")}}
     [:table
      [:tr
       [:td.question
        {:colSpan 3
         :className (name status)}
        question]]
      [:tr.options
       [:td [:a {:href "#" :on-click (choice-handler 1)} option1]]
       [:td [:a {:href "#" :on-click (choice-handler 2)} option2]]
       [:td [:a {:href "#" :on-click (choice-handler 3)} option3]]]]]))

(defn- game-layout [ch tasks l t s]
  [:div#scene
   [score-board]
   [game-board ch l t s]
   [game-control l t s]
   [ipad-control tasks l t s]])

(def scene
  (with-meta game-layout
    {:component-did-mount initialize-game-board}))
