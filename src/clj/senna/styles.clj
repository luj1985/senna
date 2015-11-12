(ns senna.styles
  (:require [garden.def :refer [defrule defstyles]]
            [garden.stylesheet :refer [rule]]))

(defstyles screen
  (let [body (rule :body)]
    (body
     {:font-family "Helvetica Neue"
      :font-size   "16px"
      :padding "8px"
      :margin 0
      :line-height 1.5}))
  [:.dashboard {
    :display "inline-block"
    :line-height "50px"
    :width "150px" }]

  [:.rounds {
    :display "inline-block"
    :line-height "50px"
    :width "150px" }]

  [:#statusbar {
    :position "fixed"
    :top 0
    :width "100%" }]

  [:#container {
    :zoom 0.5
    :margin-top "50px"
    :background-image "url(track.jpeg)"
    :background-position "0 20px"
    :background-repeat "no-repeat"}]

  [:#car {
    :fill "white"
    :stroke "#000"
    :stroke-width "1px" }]

  [:#track {
    :fill "none"
    :stroke "none"
    :stroke-width "1px" }])
