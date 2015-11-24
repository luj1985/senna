(set-env!
 :source-paths    #{"src/server" "src/cljs" "src/clj"}
 :resource-paths  #{"resources"}
 :dependencies '[[adzerk/boot-cljs              "1.7.170-3"  :scope "test"]
                 [adzerk/boot-cljs-repl         "0.3.0"      :scope "test"]
                 [adzerk/boot-reload            "0.4.2"      :scope "test"]
                 [pandeiro/boot-http            "0.7.0"      :scope "test"]
                 [org.martinklepsch/boot-garden "1.2.5-7"    :scope "test"]
                 [com.cemerick/piggieback       "0.2.1"      :scope "test"]
                 [org.clojure/tools.nrepl       "0.2.12"     :scope "test"]
                 [weasel                        "0.7.0"      :scope "test"]

                 ;; after build, clojurescript/garden will be translated
                 ;; into assets, no need to include them in war file
                 [garden "1.3.0-SNAPSHOT" :scope "client"]
                 [reagent "0.5.1" :scope "cli ent"]
                 [org.clojure/clojurescript "1.7.170" :scope "client"]
                 [org.clojure/core.async "0.2.374" :scope "client"]
                 [cljs-http "0.1.38"]

                 [org.clojure/java.jdbc "0.4.2"]
                 [ring/ring-json "0.4.0"]
                 [ring/ring-core "1.4.0"]
                 [mysql/mysql-connector-java "5.1.37"]
                 [hiccup "1.0.5"]
                 [compojure "1.4.0"]])

(require
 '[adzerk.boot-cljs      :refer [cljs]]
 '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]]
 '[adzerk.boot-reload    :refer [reload]]
 '[pandeiro.boot-http    :refer [serve]]
 '[org.martinklepsch.boot-garden :refer [garden]])

(deftask build []
  (comp (speak)
        (cljs)
        (garden :styles-var 'senna.styles/screen
                :output-to "public/css/garden.css")))

(deftask run []
  (comp (serve :handler 'senna.core/handler
               :reload true)
        (watch)
        (cljs-repl)
        (reload)
        (build)))

(deftask production []
  (task-options! cljs {:optimizations :advanced}
                 garden {:pretty-print false})
  identity)

(deftask development []
  (task-options! cljs {:optimizations :none
                       :source-map true
                       :pretty-print true}
                 reload {:on-jsload 'senna.app/init})
  identity)

(deftask dist []
  (comp (production)
        (build)
        (aot :all true)
        (web :serve 'senna.core/handler)
        (uber :exclude-scope #{"provided"})
        (war :file "senna.war")))

(deftask dev
  "Simple alias to run application in development mode"
  []
  (comp (development)
        (run)))
