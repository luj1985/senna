(ns senna.app
  (:require
    [senna.loader :as loader]
    [senna.game :as game]))

(defn init []
  (loader/init)
  (game/init))