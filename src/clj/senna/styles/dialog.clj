(ns senna.styles.dialog
  (:require
   [garden.def :refer [defstyles]]
   [garden.units :refer [px percent em]]
   [garden.color :refer [rgba]]))

(defstyles dialog-dimmer
  [:.dimmer {:position :fixed
             :z-index 4
             :top 0
             :left 0
             :width (percent 100)
             :height (percent 100)
             :background-color (rgba 0 0 0 0.7)}])

(defstyles game-rules-dialog
  [:#rules{:position :relative
           :height (percent 100)
           :width (percent 100)
           :background-image "url(../img/rules/rules.png)"
           :background-repeat :no-repeat
           :background-position "center center"
           :background-size "100% auto"}

   [:section {:color :white
              :font-size (em 1)
              :margin 0
              :padding (px 35)}]

   [:.container {:text-align :center}]

   [:button.got-it {:display :inline-block
                    :height (px 50)
                    :width (px 100)
                    :margin-top (px 50)
                    :background-color :transparent
                    :border :none
                    :background-size "100% 100%"
                    :background-image "url(../img/rules/got-it.svg)"}]])

(defstyles dialog
  dialog-dimmer
  game-rules-dialog)
