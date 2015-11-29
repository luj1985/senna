(ns senna.sound
  (:require
   [reagent.core :as r]))

(defonce mute-state (r/atom true))

(defn play-sound [id]
  (if (not @mute-state)
    (if-let [el (.getElementById js/document id)]
      (.play el))))

(defn stop-sound [id]
  (when-let [el (.getElementById js/document id)]
    (.pause el)
    (set! (.-currentTime el) 0)))

(defn pause-sound [id]
  (if-let [el (.getElementById js/document id)]
    (.pause el)))

(defn play-bgm []
  (let [action (if @mute-state pause-sound play-sound)]
    (action "m-bgm")))

(defn- toggle-sound []
  (reset! mute-state (not @mute-state))
  (play-bgm))

(defn volume-component []
  [:div.volume
   (let [muted (if @mute-state "muted" "unmuted")]
     [:a.control {:className muted
                  :href "#"
                  :on-click toggle-sound}])
   [:audio {:id "m-countdown" :src "audio/countdown.mp3"}]
   [:audio {:id "m-correct" :src "audio/correct.mp3"}]
   [:audio {:id "m-wrong" :src "audio/wrong.mp3"}]
   [:audio {:id "m-start" :src "audio/start.mp3"}]
   [:audio {:id "m-bgm" :src "audio/bgm.mp3" :autoPlay true :loop true}]])

(def volume-control
  (with-meta volume-component
    {:component-did-mount play-bgm}))

(defn mute []
  (if-let [el (.getElementById js/document "m-bgm")]
    (.pause el)))
