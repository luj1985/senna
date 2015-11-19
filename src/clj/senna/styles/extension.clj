(ns senna.styles.extension
  (:require
   [garden.selectors :as s :refer [defpseudoelement]]))

(defpseudoelement -webkit-progress-bar)
(defpseudoelement -webkit-progress-value)
(defpseudoelement -moz-progress-bar)
