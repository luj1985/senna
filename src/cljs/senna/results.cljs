(ns senna.results)


(defn result-page [chan params l t s]
  (js/console.log (clj->js params))
  (let [h (.-innerHeight js/window)
        h1 (* s 1152)
        offset (/ (- h h1) 2)
        time (:time params)
        min (js/parseInt (/ time 60))
        secs (js/parseInt (mod time 60))]
    [:div#score
     [:section {:style {:padding-top (str (+ 150 offset) "px")}}
      [:div.usage
       [:span (str "用时："min "分" secs "秒")]]
      [:div.rank
       [:div.global "全球排名：" (:global params)]
       [:div.best "历史最好：" (:best params)]]
      [:div.container]]]))
