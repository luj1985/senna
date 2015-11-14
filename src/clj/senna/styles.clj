(ns senna.styles
  (:require 
    [garden.core :refer [css]]
    [garden.def :refer [defrule defstyles]]
    [garden.stylesheet :refer [rule]]
    [garden.units :as u :refer [px pt]]
    [garden.color :as color :refer [hsl rgb]]))

(defstyles screen
  [:body {:font-family "Helvetica Neue"
          :font-size (px 16)
          :padding (px 8)
          :margin 0
          :line-height 1.5}]

  [:.dashboard :.rounds { :display :inline-block
                          :line-height (px 50)
                          :width (px 150) }]

  [:#statusbar {:position :fixed
                :top 0
                :width "100%" }]

  [:#container {:zoom 0.5
                :margin-top (px 50)
                :background-image "url(track.jpeg)"
                :background-position "0 20px"
                :background-repeat "no-repeat"}]

  [:#car {:fill :none
          :stroke (rgb 0 0 0)
          :stroke-width (px 1) }]

  [:#track {:fill :none
            :stroke :none
            :stroke-width (px 1) }])
