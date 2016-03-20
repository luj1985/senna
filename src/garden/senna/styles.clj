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
    [senna.styles.countdown :refer [countdown-styles]]
    [senna.styles.presentation :refer [presentation-styles]]))

(defstyles base-styles
  [:body {:font-family "Helvetica Neue"
          :font-size (px 16)
          :padding 0
          :margin 0
          :line-height 1.5}]
  [:a.tel {:color :white
           }])

(defstyles button-styles
  [:a.button :button "input[type=\"submit\"]"
   {:padding "5px 10px"
    :text-decoration :none
    :font-size (px 16)
    :outline :none}

   ["&.black" {:background-color :transparent
               :background-image "url(../img/dialog/btn.png)"
               :background-repeat :no-repeat
               :background-size "100% 100%"
               :padding "12px 20px"
               :color :white
               :border :none}]])

(defstyles screen
  base-styles
  dialog-styles
  countdown-styles
  loading-page-styles
  game-board-styles
  presentation-styles
  button-styles

  [:.actions {:text-align :center
              :margin-bottom (px 50)}]
  [:section.back {:text-align :center
                  :margin-top (px 20)}]

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
             :border-collapse :collapse
             :width (percent 100)
             :height (percent 100)}]]

   [:.message {:color :white
               :padding (px 10)
               :font-size (px 60)}]

   [:.question {:padding "20px 10px 30px 10px"
                :transition "background-color cubic-bezier(.62,.28,.23,.99) .5s"
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
          :border "1px solid black"
          :background-color (rgb 89 90 91)}]

    [:a {:font-size (px 30)
         :padding (px 10)
         :display :inline-block
         :text-align :left
         :text-decoration :none
         :color :white}]]

   [:.main {:background-image "url(../img/game/background.png)"
            :background-size "100% auto"
            :background-repeat :no-repeat
            :background-position "center center"
            :position :relative
            :height (percent 100)}]]

  [:#track {:fill :none
            :stroke :none
            :stroke-width (px 3) }])
