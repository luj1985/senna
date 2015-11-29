(ns senna.styles.dialog
  (:require
   [garden.def :refer [defstyles]]
   [garden.selectors :refer [& before]]
   [garden.units :refer [px percent em]]
   [garden.color :refer [rgba rgb]]))

(defstyles dialog-dimmer
  [:.dimmer {:position :fixed
             :text-align :center
             :top 0
             :left 0
             :width (percent 100)
             :height (percent 100)
             :background-color (rgba 0 0 0 0.7)}]

  [:.content {:display :inline-block
              :color :white
              :margin-top (percent 25)
              :padding "50px 25px 0 25px"
              :width (percent 85)
              :height (percent 100)
              :border-radius (px 30)
              :box-sizing :border-box
              :text-align :left}

   [:section {:text-align :left
              :display :block
              :margin-bottom (px 50)}]

   [:button {:background-color (rgb 214 11 12)
             :background "linear-gradient(180deg,rgb(214,11,12) 0%, rgb(126,16,16) 100%)"
             :color (rgb 251 235 14)
             :padding "5px 10px"
             :font-size (px 16)
             :outline :none
             :border "2px solid yellow"
             :border-radius (px 30)}]])

(defstyles game-score-dialog
  [:#score {:background-image "url(../img/dialog/scores.png)"
            :background-size "100% auto"
            :background-repeat :no-repeat
            :background-position "top center"
            :text-align :center}]

  [:.usage {:text-align :center}
   [(& before) {:content "''"
                :background-image "url(../img/dialog/achieve.png)"
                :background-size "100% 100%"
                :position :absolute
                :-webkit-transform "translate(-40px,-15px)"
                :transform "translate(-40px,-15px)"
                :display :inline-block
                :width (px 60)
                :height (px 60)}]
   [:span {:color :white
           :padding "5px 20px 5px 20px"
           :background-color (rgb 230 1 18)
           :font-size (px 20)}]]

  [:.rank {:padding-top (px 30)
           :text-align :center}]

  [:.global {:color :white
             :font-size (px 20)}]
  [:.best {:color :white
           :font-size (px 20)}])


(defstyles game-rules-dialog
  [:#rules {:background-image "url(../img/dialog/rules.png)"
            :background-size "100% auto"
            :background-repeat :no-repeat
            :background-position "top center"
            :text-align :center}

   [(& before) {:content "attr(title) ' '"
                :position :absolute
                :display :inline-block
                :width (px 64)
                :height (px 24)
                :padding "5px 30px"
                :border-radius (px 32)
                :border-style :dashed
                :-webkit-transform "translate(-50%,-70px)"
                :transform "translate(-50%,-70px)"
                :background-color (rgb 234 2 3)
                :background "linear-gradient(180deg,rgb(244,3,2) 0%, rgb(128,15,16) 100%)"}]])

(defstyles dialog-styles
  dialog-dimmer
  game-score-dialog
  game-rules-dialog)
