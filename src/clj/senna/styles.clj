(ns senna.styles
  (:require
    [garden.core :refer [css]]
    [garden.def :refer [defrule defstyles]]
    [garden.stylesheet :refer [rule]]
    [garden.units :as u :refer [px pt percent]]
    [garden.color :as color :refer [hsl rgb rgba]]))

(defstyles screen
  [:body {:font-family "Helvetica Neue"
          :font-size (px 16)
          :padding 0
          :margin 0
          :line-height 1.5}]

  [:#loading {:position "fixed"
              :top 0
              :bottom 0
              :width "100%"
              :background-position "top center"
              :background-size "100% auto"
              :background-image "url(../img/loading/loading.png)"}

   [:div.logo {:position "fixed"
               :top (px 16)
               :right (px 16)
               :width (px 60)
               :height (px 60)
               :background-image "url(../img/loading/CAFlogo.jpg)"
               :background-size "100% auto"
               :background-position "center center"}]

   [:div.container {:position :fixed
                    :bottom (px 60)
                    :display :block
                    :width (percent 100)}

    [:div.progressbar {:position :relative
                       :width (percent 60)
                       :background-color (rgba 0 0 0 0.8)
                       :padding (px 2)
                       :border-radius (px 7)
                       :margin "0 auto"}]

    [:div.progress {:height (px 12)
                    :width (percent 100)
                    :border-radius (px 5)
                    :background-color (rgb 27 154 254)
                    :background-repeat :repeat-x
                    :background-size "16px 12px"
                    :background-image "linear-gradient(315deg,transparent,transparent 33%,rgba(0, 0, 0, 0.12) 33%,rgba(0, 0, 0, 0.12) 66%,transparent 66%,transparent)"
                   }]]]


  [:.dashboard :.rounds { :display :inline-block
                          :line-height (px 50)
                          :width (px 150) }]

  [:#statusbar {:position :fixed
                :top 0
                :width (percent 100) }]

  [:#container {:zoom 0.5
                :background-image "url(../img/game/track.jpg)"
                :background-position "0 70px"
                :background-repeat :no-repeat}]

  [:#car {:fill :none
          :stroke (rgb 0 0 0)
          :stroke-width (px 1) }]

  [:#track {:fill :none
            :stroke :none
            :stroke-width (px 1) }])
