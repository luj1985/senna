(ns senna.styles.game
  (:require
   [garden.stylesheet :refer [at-font-face]]
   [garden.def :refer [defstyles]]
   [garden.units :refer [px percent]]))

(defstyles dashboard
  [:.dashboard {:background-image "url(../img/game/dashboard.png)"
                :background-size "100% 100%"
                :display :inline-block
                :float :left
                :width (px 80)
                :height (px 80)}])

(defstyles dashboard-pointer
  [:.pointer {:background-image "url(../img/game/pointer.svg)"
              :background-size "100% 100%"
              :position :absolute
              :-webkit-transform "rotate(-110deg)"
              :transform "rotate(-110deg)"
              :height (px 80)
              :width (px 80)}])

(defstyles speed-board
  dashboard
  dashboard-pointer)


(defstyles round-counter
  [:.round {:background-size "auto 38px"
            :background-repeat :no-repeat
            :margin-top (px 18)
            :width (px 50)
            :height (px 50)
            :display :inline-block}])


(defstyles round-prefix
  [:.prefix {:background-image "url(../img/game/round.svg)"
             :background-size "auto 25px"
             :background-repeat :no-repeat
             :float :left
             :margin-top (px 30)
             :display :inline-block
             :height (px 50)
             :width (px 105)}])

(defstyles round-dashboard
  round-prefix
  round-counter
  [:.rounds {:display :inline-block
             :min-width (px 150)}])

(defstyles timer
  [:.timer {:position :absolute
            :font-family "digital-7"
            :font-size (px 45)
            :background-color :transparent
            :display :inline-block
            :text-align :center
            :height (px 80)
            :width (px 160)
            :left (px 555)
            :transform "rotate(-14deg)"}
   [:.ms {:font-size (px 36)}]])

(defstyles volume
  [:.volume {:background-image "url(../img/game/volume.svg)"
             :background-size "30px 30px"
             :margin-top (px 25)
             :margin-right (px 20)
             :float :right
             :display :inline-block
             :height (px 30)
             :width (px 30)}])

(defstyles game-board-styles
  [(at-font-face
    {:font-family "digital-7"
     :src "url('digital-7/digital-7 (mono).ttf') format('truetype')"})]

  [:.score-board {:position :fixed
                  :top 0
                  :z-index 2
                  :width (percent 100)}]

  speed-board
  round-dashboard
  timer
  volume)
