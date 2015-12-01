(ns senna.styles.presentation
  (:require
   [garden.def :refer [defstyles]]
   [garden.units :refer [px percent em]]
   [garden.color :refer [rgb]]))

(defstyles presentation-styles
  [:.presentation {:padding 0
                   :margin 0}
   [:img.header {:width (percent 100)}]

   [:p {:margin (px 24)}]

   [:h3 {:font-size (px 24)
         :margin "50px 24px 0 24px"
         :text-align :center
         :color :white
         :padding (px 10)
         :background-color (rgb 232 0 21)}]
   [:h4 {:font-size (px 24)
         :color (rgb 232 0 21)
         :text-align :center}]
   [:em {:color (rgb 232 0 21)
         :font-weight :bold
         :font-style :normal}]

   [:table {:background-color (rgb 233 233 233)
            :border-collapse :seperate
            :border-spacing (px 4)
            :margin "0 24px 50px 24px"}
    [:td {:box-sizing :border-box}]
    [:img {:width (percent 100)
           :float :left}]]
   [:footer.fixed {:position :fixed
                   :width (percent 100)
                   :line-height (px 50)
                   :bottom 0
                   :background-color (rgb 232 0 21)
                   :color :white
                   :text-align :center}]])