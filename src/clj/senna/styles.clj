(ns senna.styles
  (:require
    [garden.def :refer [defstyles]]
    [garden.stylesheet :refer [rule at-font-face]]
    [garden.units :refer [px pt percent]]
    [garden.color :refer [rgb rgba]]
    [garden.selectors :refer [& nth-of-type]]
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
            :display :inline-block
            :font-size (px 20)
            :height (px 356)
            :width (px 466)
            :left (px 259)}

    [:table {:table-layout :fixed
             :width (percent 100)
             :height (percent 100)}]]

   [:.message {:color :white
               :padding (px 10)
               :font-size (px 60)}]

   [:.question {:padding "20px 10px 30px 10px"
                :-webkit-transition "background-color cubic-bezier(.62,.28,.23,.99) .9s"
                :transition "background-color cubic-bezier(.62,.28,.23,.99) .9s"
                :vertical-align :top
                :margin 0
                :height (px 120)
                :font-size (px 32)}

    ["&.normal" {:background-color (rgb 243 205 1)}]
    ["&.correct" {:background-color (rgb 0 178 78)}]
    ["&.wrong" {:background-color (rgb 233 21 49)}]]

   [:.options {:margin 0
               :padding 0
               :height (px 186)}
    [:td {:text-align :center
          :vertical-align :center}]

    [:a {:font-size (px 30)
         :padding (px 10)
         :display :inline-block
         :text-align :left
         :text-decoration :none
         :color :white}]

    [:.option1 {:background-color (rgb 180 0 139)}]
    [:.option2 {:background-color (rgb 18 117 185)}]
    [:.option3 {:background-color (rgb 0 82 156)}]]

   [:.main {:background-image "url(../img/game/background.png)"
            :background-size "100% auto"
            :background-repeat :no-repeat
            :background-position "center center"
            :position :relative
            :height (percent 100)}]]

  [:#track {:fill :none
            :stroke :none
            :stroke-width (px 3) }])
