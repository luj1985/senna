(ns senna.styles
  (:require
    [garden.core :refer [css]]
    [garden.def :refer [defrule defstyles defkeyframes]]
    [garden.selectors :as s :refer [defpseudoelement]]
    [garden.stylesheet :refer [rule at-font-face]]
    [garden.units :as u :refer [px pt percent]]
    [garden.color :as color :refer [rgb rgba]]
    [senna.styles.loading :as l]))

(defpseudoelement -webkit-progress-bar)
(defpseudoelement -webkit-progress-value)
(defpseudoelement -moz-progress-bar)

(defkeyframes dash
  [:to
   {:stroke-dashoffset 0}])

(defstyles screen

  [(at-font-face {:font-family "digital-7"
                 :src "url('digital-7/digital-7 (mono).ttf') format('truetype')"})]

  [:body {:font-family "Helvetica Neue"
          :font-size (px 16)
          :padding 0
          :margin 0
          :line-height 1.5}]

  l/loading-page

  [:#scene {:position :fixed
            :top 0
            :bottom 0
            :width (percent 100)
            :overflow :hidden}

   [:.score-board {:position :fixed
                   :top 0
                   :z-index 2
                   :width (percent 100)}
    [:.dashboard {:background-image "url(../img/game/dashboard.png)"
                  :background-size "100% 100%"
                  :display :inline-block
                  :float :left
                  :width (px 80)
                  :height (px 80)}]

    [:.pointer {:background-image "url(../img/game/pointer.svg)"
                :background-size "100% 100%"
                :position :absolute
                :-webkit-transform "rotate(-110deg)"
                :transform "rotate(-110deg)"
                :height (px 80)
                :width (px 80)}]

    [:.rounds {:display :inline-block
               :min-width (px 150)}]

    [:.prefix {:background-image "url(../img/game/round.svg)"
               :background-size "auto 25px"
               :background-repeat :no-repeat
               :float :left
               :margin-top (px 30)
               :display :inline-block
               :height (px 50)
               :width (px 105)}]

    [:.round {:background-size "auto 38px"
              :background-repeat :no-repeat
              :margin-top (px 18)
              :width (px 50)
              :height (px 50)
              :display :inline-block}]

    [:.volume {:background-image "url(../img/game/volume.svg)"
               :background-size "30px 30px"
               :margin-top (px 25)
               :margin-right (px 20)
               :float :right
               :display :inline-block
               :height (px 30)
               :width (px 30)}]]

   [:.timer {:position :absolute
             :font-family "digital-7"
             :font-size (px 60)
             :background-color :transparent
             :display :inline-block
             :text-align :center
             :height (px 80)
             :width (px 148)
             :left (px 562)
             :-webkit-transform "rotate(-14deg)"
             :transform "rotate(-14deg)"
             }]
   [:.ipad {:position :absolute
            :height (px 356)
            :width (px 465)
            :left (px 259)
            :display :inline-block}]
   [:.question {:padding "20px 10px 30px 10px"
                :margin 0
                :height (px 120)
                :background-color (rgb 243 205 1)
                :font-size (px 32)}]
   [:.options {:margin 0
               :padding 0
               :display :flex
               :height (px 186)
               :list-style :none
               }
    [:a {:text-decoration :none
         :font-size (px 30)
         :display :block
         :color :white}]

    [:li {:height (percent 100)
          :min-width (px 100)
          :display :flex
          :align-items :center
          :justify-content :center
          :flex-grow 1}
     ["&:nth-of-type(1)" {:background-color (rgb 180 0 139)}]
     ["&:nth-of-type(2)" {:background-color (rgb 18 117 185)}]
     ["&:nth-of-type(3)" {:background-color (rgb 0 82 156)}]
     ]]

   [:.main {:background-image "url(../img/game/track.png),
                               url(../img/game/background.jpg)"
            :background-size "100% auto"
            :background-repeat :no-repeat
            :background-position "center center"
            :position :relative
            :height (percent 100)
            }]

   [:.dialog {:position :fixed
              :z-index 4
              :top 0
              :left 0
              :width (percent 100)
              :height (percent 100)
              :background-color (rgba 0 0 0 0.7)
              }]]

  [:#car {:fill :none
          :stroke (rgb 0 0 0)
          :stroke-width (px 1) }]

  ;; TODO: extract common styles, like center (horizontal+vertical)
  [:#countdown {:position :relative
                :height (percent 100)
                :width (percent 100)}
   [:svg.loader {:position :absolute
                 :width (px 160)
                 :height (px 160)
                 :transform "translate(-50%,-50%)"
                 :top (percent 50)
                 :left (percent 50)
               }]
   [:path {:fill :none
           :box-sizing :border-box
           :stroke-width (px 60)
           :animation [[dash "1s" :linear 3]]}]

   [:circle {:transform "translate(50%,50%)"}]

   [:.seconds {:font-size (px 40)}]
   [:.go {:font-size (px 30)}]
   [:.txt {:color :white
               :border-radius (percent 50)
               :border "3px solid white"
               :position :absolute
               :transform "translate(-50%,-50%)"
               :top (percent 50)
               :left (percent 50)
               :display :flex
               :align-items :center
               :justify-content :center
               :background-color :black
               :text-align :center
               :z-index 5
               :height (px 60)
               :width (px 60)
               }]]

  [:#rules{:position :relative
           :height (percent 100)
           :width (percent 100)
           :background-image "url(../img/rules/rules.png)"
           :background-repeat :no-repeat
           :background-position "center center"
           :background-size "100% auto"}

   [:section {:color :white
        :font-size "1em"
        :margin 0
        :padding (px 35)}

    [:.container {:text-align :center}]

    [:button.got-it {:display :inline-block
                     :height (px 50)
                     :width (px 100)
                     :margin-top (px 50)
                     :background-color :transparent
                     :border :none
                     :background-size "100% 100%"
                     :background-image "url(../img/rules/got-it.svg)"}]]

   ]

  [:#track {:fill :none
            :stroke :none
            :stroke-width (px 3) }])
