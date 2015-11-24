(ns senna.styles
  (:require
    [garden.def :refer [defstyles]]
    [garden.stylesheet :refer [rule at-font-face]]
    [garden.units :refer [px pt percent]]
    [garden.color :refer [rgb rgba]]
    [senna.styles.game :refer [game-board-styles]]
    [senna.styles.loading :refer [loading-page-styles]]
    [senna.styles.dialog :refer [dialog-styles]]
    [senna.styles.countdown :refer [countdown-styles]]))

(defstyles base-styles
  [:body {:font-family "Helvetica Neue"
          :font-size (px 16)
          :padding 0
          :margin 0
          :line-height 1.5}])

(defstyles screen
  base-styles
  dialog-styles
  countdown-styles
  loading-page-styles
  game-board-styles

  [:#scene {:position :fixed
            :top 0
            :bottom 0
            :width (percent 100)
            :overflow :hidden}

   [:.ipad {:position :absolute
            :height (px 356)
            :width (px 465)
            :left (px 259)
            :display :inline-block}]
   [:.message {:color :white
               :padding (px 10)
               :font-size (px 60)}]

   [:.question {:padding "20px 10px 30px 10px"
                :margin 0
                :height (px 120)
                :background-color (rgb 243 205 1)
                :font-size (px 32)}]

   [:.options {:margin 0
               :padding 0
               :display :flex
               :height (px 186)
               :list-style :none}

    [:a {:font-size (px 30)
         :text-decoration :none
         :padding (px 10)
         :color :white}]

    [:li {:height (percent 100)
          :min-width (px 100)
          :display :flex
          :align-items :center
          :justify-content :center
          :width (percent 33.33333)}
     ["&:nth-of-type(1)" {:background-color (rgb 180 0 139)}]
     ["&:nth-of-type(2)" {:background-color (rgb 18 117 185)}]
     ["&:nth-of-type(3)" {:background-color (rgb 0 82 156)}]
     ]]

   [:.main {:background-image "url(../img/game/background.jpg)"
            :background-size "100% auto"
            :background-repeat :no-repeat
            :background-position "center center"
            :position :relative
            :height (percent 100)
            }]

   ]

  [:#car {:fill :none
          :stroke (rgb 0 0 0)
          :stroke-width (px 1) }]

  [:#track {:fill :none
            :stroke :none
            :stroke-width (px 3) }])
