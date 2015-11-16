(ns senna.rules
  (:require
   [reagent.core :as r]
   [cljs.core.async :as async :refer [>! <!]])
  (:require-macros [cljs.core.async.macros :refer [go]]))



(defn- rules-page []
  [:div#rules.dialog
   [:div.container
    [:div.title]
    [:div.content]]])


(defn init [])
