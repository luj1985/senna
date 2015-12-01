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

  [:.fullscreen {:height (percent 100)
                 :width (percent 100)
                 :background-color :white}]

  [:.content {:display :inline-block
              :color :white
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
             :background-position "left center"}]]])


(defstyles responsive-dialog
  [(at-media {:max-device-height (px 480)}
             [:.content {:margin-top (px 25)}])]

  [(at-media {:min-device-height 481 :max-device-height (px 568)}
             [:.content {:margin-top (px 60)}])]

  [(at-media {:min-device-height (px 579)}
             [:.content {:margin-top (percent 25)}])])


(defstyles score-dialog-media
  [(at-media {:max-width (px 330)}
             [:.more {:margin-top (px 30)}])]

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

   [:.usage {:font-size (px 30)
             :white-space :nowrap}
    [:.txt {:font-size (px 15)
            :padding "0 2px"}]]
   [:.rank {:font-size (px 12)
            :white-space :nowrap}
    [:.history {:font-size (px 18)
                :padding "0 2px"}
     [:.best {:font-size (px 18)}
      [:.txt {:font-size (px 12)
              :padding "0 2px"}]]]]])

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

(defstyles social-page
  [:#panel {:text-align :center}]
  [:#copy {:display :none}]
  [:#social-link {:color :transparent}]
  [:#social {:width (percent 100)
             :position :fixed
             :bottom 0
             :background :white
             :box-sizing :border-box}

   [:h4 {:color (rgb 86 86 86)
         :margin (px 16)}]
   [:h5 {:margin "10px 0"}]
   [:table {:width (percent 100)
            :vertical-align :top
            :table-layout :fixed}]
   [:a.share {:color (rgb 139 139 139)
              :text-decoration :none}]
   [:.cancel {:color (rgb 133 133 133)
              :background-color (rgb 236 236 236)
              :border :none
              :margin-top (px 10)
              :height (px 50)
              :width (percent 100)}]])

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
  score-dialog-media
  responsive-dialog
  social-page)
