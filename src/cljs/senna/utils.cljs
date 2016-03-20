(ns senna.utils)

(defn- padding-number [digit]
  (if (< digit 10)
    (str "0" digit)
    digit))

(defn parse-time [time]
  (let [mins (js/parseInt (/ time 60000))
        mss (-> (mod time 1000)
                (/ 10)
                js/parseInt
                padding-number)
        secs (-> (/ time 1000)
                 js/parseInt
                 (mod 60)
                 padding-number)]
    {:mins mins
     :secs secs
     :mss mss}))
