(ns senna.users
  "Used to track current user")

(defn get-uid []
  (.getItem js/localStorage "uid"))

(defn set-uid [uid]
  (.setItem js/localStorage "uid" uid))

(defn get-uid-headers []
  (let [uid (get-uid)]
    (if uid {:headers {"UID" uid}} {})))
