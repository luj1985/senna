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
              :padding "30px 25px 0 25px"
              :width (percent 85)
              :height (percent 100)
              :border-radius (px 30)
              :box-sizing :border-box
              :text-align :left}

   [:section {:text-align :left
              :display :block
              :margin-bottom (px 20)}
    ["&.important" {:font-weight :bold
                    :font-size (em 1.1)}]]

   [:button {:padding "5px 10px"
             :font-size (px 16)
             :outline :none}

    ["&.black" {:background-color :transparent
                :background-image "url(../img/dialog/btn.png)"
                :background-size "100% auto"
                :padding "10px 20px"
                :color :white
                :border :none}]

    ["&.red" {:background-color (rgb 214 11 12)
              :background "linear-gradient(180deg,rgb(214,11,12) 0%, rgb(126,16,16) 100%)"
              :border "1px solid yellow"
              :color (rgb 251 235 14)
              :border-radius (px 30)}]

    ["&.yellow" {:background-color (rgb 255 218 115)
                 :border "1px solid rgb(226,120,3)"
                 :color (rgb 180 0 1)
                 :border-radius (px 30)}]]])

(defstyles game-score-dialog
  [:#score {:background-image "url(../img/dialog/scores.png)"
            :background-size "100% auto"
            :background-repeat :no-repeat
            :background-position "top center"
            :text-align :center
            :padding-top 0}]

  [:.score {:padding-left (px 85)
            :padding-top (px 60)
            :background-image "url(../img/dialog/achieve.png)"
            :background-repeat :no-repeat
            :background-size "80px auto"}]

  [:.usage {:display :inline-block}
   [:span {:color :white
           :background-color (rgb 230 1 18)
           :font-size (px 35)}]]

  [:.rank {:display :inline-block}]

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
                :-webkit-transform "translate(-50%,-50px)"
                :transform "translate(-50%,-50px)"
                :background-color (rgb 234 2 3)
                :background "linear-gradient(180deg,rgb(244,3,2) 0%, rgb(128,15,16) 100%)"}]])

(defstyles dialog-styles
  dialog-dimmer
  game-score-dialog
  game-rules-dialog)
