(ns senna.styles.loading
  (:require
   [garden.selectors :as s :refer [defpseudoelement]]
   [garden.def :refer [defstyles]]
   [garden.color :refer [rgb rgba]]
   [garden.units :refer [px percent]])
  (:use
   [senna.styles.extension]))

(defstyles loading-progress-bar
  [(s/progress) {:position :relative
                 :display :block
                 :box-sizing :content-box
                 :border "2px solid rgba(0,0,0,0.8)"
                 :background-color (rgba 0 0 0 0.8)
                 :border-radius (px 4)
                 :-webkit-appearance :none
                 :appearance :none
                 :width (percent 60)
                 :height (px 12)
                 :margin "0 auto"}

   [(s/& -webkit-progress-bar)
    {:background-color (rgba 0 0 0 0.8)
     :border-radius (px 2)
     :box-shadow "0 2px 5px rgba(0,0,0,0.25) inset"}]

   [(s/& -webkit-progress-value)
    {:border-radius (px 2)
     :background-color (rgb 27 154 254)
     :background-repeat :repeat-x
     :background-size [[(px 16) (px 12)]]
     :background-image "linear-gradient(315deg,transparent,transparent 33%,
                                          rgba(0, 0, 0, 0.12) 33%,
                                          rgba(0, 0, 0, 0.12) 66%,
                                          transparent 66%,
                                          transparent)"}]
   ;; Android 4.1-4.3 support
   ;; TODO: use auto-prefix ?
   [(s/& -webkit-progress-value)
    {:background-image "-webkit-linear-gradient(315deg,transparent,transparent 33%,
                                          rgba(0, 0, 0, 0.12) 33%,
                                          rgba(0, 0, 0, 0.12) 66%,
                                          transparent 66%,
                                          transparent)"}]

   ;; Firefox provides a single pseudo class (-moz-progress-bar)
   ;; https://css-tricks.com/html5-progress-element/
   [(s/& -moz-progress-bar)
    {:border-radius (px 2)
     :background-color (rgb 27 154 254)
     :background-repeat :repeat-x
     :background-size [[(px 16) (px 12)]]
     :background-image "linear-gradient(315deg,transparent,transparent 33%,
                                          rgba(0, 0, 0, 0.12) 33%,
                                          rgba(0, 0, 0, 0.12) 66%,
                                          transparent 66%,
                                          transparent)"}]])

(def loading-page
  [:#loading {:position :fixed
              :top 0
              :bottom 0
              :width (percent 100)
              :background-position "top center"
              :background-size "100% auto"
              :background-image "url(../img/loading/loading.jpg)"}

   [:.logo {:position :fixed
            :top (px 16)
            :right (px 16)
            :width (px 60)
            :height (px 60)
            :background-image "url(../img/loading/CAFlogo.jpg)"
            :background-size "100% 100%"
            :background-position "center center"}]

   [:.progress-bar {:position :fixed
                    :bottom (px 60)
                    :width (percent 100)}]

   loading-progress-bar])
