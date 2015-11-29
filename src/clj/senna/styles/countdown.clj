(ns senna.styles.countdown
  (:refer-clojure :exclude [&])
  (:require
   [garden.selectors :refer [&]]
   [garden.def :refer [defstyles defkeyframes]]
   [garden.units :refer [px percent em]]
   [garden.color :refer [rgba rgb]]))

(defkeyframes countdown
  [:to
   {:stroke-dashoffset 0}])

(defstyles countdown-styles
  countdown

  [:#countdown {:position :relative
                :height (percent 100)
                :width (percent 100)}
   [:svg.loader {:position :absolute
                 :width (px 160)
                 :height (px 160)
                 :-webkit-transform "translate(-50%,-50%)"
                 :transform "translate(-50%,-50%)"
                 :top (percent 50)
                 :left (percent 50)}]

   [:path {:fill :none
           :stroke-width (px 60)
           :-webkit-animation [[countdown "1s" :linear 3]]
           :animation [[countdown "1s" :linear 3]]}]

   [:circle {:-webkit-transform "translate(50%,50%)"
             :transform "translate(50%,50%)"}]

   [:.seconds {:font-size (px 40)}]

   [:.go {:font-size (px 30)}]

   [:.txt {:color :white
           :border-radius (percent 50)
           :border "3px solid white"
           :position :absolute
           :-webkit-transform "translate(-50%,-50%)"
           :transform "translate(-50%,-50%)"
           :top (percent 50)
           :left (percent 50)
           :height (px 60)
           :width (px 60)
           :display :flex
           :align-items :center
           :justify-content :center
           :background-color :black
           :text-align :center
           :z-index 5}]])
