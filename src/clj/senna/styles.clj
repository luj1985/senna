(ns senna.styles
  (:refer-clojure :exclude [&])
  (:require
    [garden.core :refer [css]]
    [garden.def :refer [defrule defstyles defkeyframes]]
    [garden.selectors :as s :refer [& defpseudoelement]]
    [garden.stylesheet :refer [rule at-font-face]]
    [garden.units :as u :refer [px pt percent]]
    [garden.color :as color :refer [rgb rgba]]
    [senna.styles.game :as g]
    [senna.styles.loading :as l]
    [senna.styles.dialog :as d]
    [senna.styles.countdown :as c]))

(defstyles base
  [:body {:font-family "Helvetica Neue"
          :font-size (px 16)
          :padding 0
          :margin 0
          :line-height 1.5}])

(defstyles screen
  base
  d/dialog
  l/loading-page
  c/countdown
  g/game-board

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
   [:.question {:padding "20px 10px 30px 10px"
                :margin 0
                :height (px 120)
                :background-color (rgb 243 205 1)
                :font-size (px 32)}]
   [:.options {:margin 0
               :padding 0
               :display :flex
               :height (px 186)
               :list-style :none
               }
    [:a {:text-decoration :none
         :font-size (px 30)
         :display :block
         :color :white}]

    [:li {:height (percent 100)
          :min-width (px 100)
          :display :flex
          :align-items :center
          :justify-content :center
          :flex-grow 1}
     ["&:nth-of-type(1)" {:background-color (rgb 180 0 139)}]
     ["&:nth-of-type(2)" {:background-color (rgb 18 117 185)}]
     ["&:nth-of-type(3)" {:background-color (rgb 0 82 156)}]
     ]]

   [:.main {:background-image "url(../img/game/track.png),
                               url(../img/game/background.jpg)"
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
