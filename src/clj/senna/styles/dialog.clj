(ns senna.styles.dialog
  (:require
   [garden.def :refer [defstyles]]
   [garden.selectors :refer [& before]]
   [garden.stylesheet :refer [at-media]]
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
              :padding "30px 20px 0 20px"
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

   [:.more {:display :inline-block
            :padding-left (px 90)
            :line-height (px 80)
            :text-decoration :none
            :white-space :nowrap
            :color :black
            :background-image "url(../img/loading/CAFlogo.jpg)"
            :background-repeat :no-repeat
            :background-size "80px auto"}
    [:.left {:padding-left (px 25)
             :display :inline-block
             :background-image "url(../img/click.png)"
             :background-repeat :no-repeat
             :background-size "25px 25px"
             :background-position "left center"}]]

   ;; TODO: make serveral selector share same rules

   [:button "input[type=\"submit\"]" {:padding "5px 10px"
             :font-size (px 16)
             :outline :none}

    ["&.black" {:background-color :transparent
                :background-image "url(../img/dialog/btn.png)"
                :background-repeat :no-repeat
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


(defstyles score-dialog-media
  [(at-media {:max-width (px 330)}
             [:.more {:margin-top (px 20)}])]

  [(at-media {:min-width (px 330)}
             [:.more {:margin-top (px 80)}]
             [:.score {:margin-left (px 20)}])]

  [(at-media {:min-width (px 400)}
             [:.more {:margin-top (px 120)}]
             [:.score {:margin-left (px 50)}])])

(defstyles game-score-dialog
  [:#score {:background-image "url(../img/dialog/scores.png)"
            :background-size "100% auto"
            :background-repeat :no-repeat
            :background-position "top center"
            :text-align :center
            :padding-top 0}]

  [:.score {:padding-left (px 90)
            :padding-top (px 60)
            :color :white
            :background-size "80px auto"
            :background-image "url(../img/dialog/achieve.png)"
            :background-repeat :no-repeat}
   [:.num {:font-size (px 30)}]
   [:.txt {:font-size (px 15)}]]

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

(defstyles tel-page-dialog
  [:#tel {:background-color (rgb 229 3 18)
          :position :relative
          :color :white
          :height :auto}]
  [:#mobile {:display :block
             :width (percent 90)
             :font-size (px 18)
             :margin "10px 0"}]
  [:#submit {:margin "10px 0 10px 0"
             :padding "5px 20px"}])

(defstyles dialog-styles
  dialog-dimmer
  game-score-dialog
  game-rules-dialog
  tel-page-dialog
  score-dialog-media)
