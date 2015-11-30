(ns senna.styles.loading
  (:require
   [garden.def :refer [defstyles]]
   [garden.units :refer [px percent]])
  (:use
   [senna.styles.extension]))

(defstyles loading-page-styles
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
            :background-position "center center"}]])
