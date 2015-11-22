(ns senna.countdown
  (:require
   [reagent.core :refer [atom]]
   [cljs.core.async :refer [put!]]))

(defonce ^:private countdown (atom 3))

(def ^:const colors ["#F9B72B" "#F39900" "#EA5412" "#E93228"])

(def ^:const radius 80)

(def ^:const inner 50)

(defn- path-circle [rx ry r]
  (str "M" rx "," ry "m" 0 ",-" r
       "a" r "," r ",0,1,1,0," (* 2 r)
       "a" r "," r ",0,1,1,0,-" (* 2 r)))

(defn- circumference [r]
  (* 2 js/Math.PI r))

(defn countdown-page [chan _ _ _]
  (let [s @countdown
        c (circumference inner)
        bg (colors s)
        fg (colors (max (dec s) 0))]

    (if (pos? s)
      (js/setTimeout #(swap! countdown dec) 1000)
      (js/setTimeout #(put! chan :start) 300))

    [:div#countdown
     [:svg.loader
      [:circle {:r radius
                :style {:fill bg}}]
      [:path {:d (path-circle radius radius inner)
              :style {:stroke-dasharray c
                      :stroke-dashoffset c
                      :stroke fg}}]]
     (if (pos? s)
       [:div.txt.seconds s]
       [:div.txt.go "GO!"])]))
