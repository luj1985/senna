(set-env!
 :source-paths    #{"src/server" "src/cljs" "src/garden"}
 :resource-paths  #{"resources"}
 :dependencies '[[adzerk/boot-cljs "1.7.228-1" :scope "boot"]
                 [adzerk/boot-cljs-repl "0.3.0" :scope "boot"]
                 [adzerk/boot-reload "0.4.5" :scope "boot"]
                 [pandeiro/boot-http "0.7.3" :scope "boot"]
                 [org.martinklepsch/boot-garden "1.3.0-0" :scope "boot"]
                 [danielsz/boot-autoprefixer "0.0.7" :scope "boot"]
                 [org.clojure/tools.nrepl "0.2.12" :scope "boot"]
                 [cpmcdaniel/boot-copy "1.0" :scope "boot"]
                 [com.cemerick/piggieback "0.2.1" :scope "boot"]
                 [weasel "0.7.0" :scope "boot"]

                 ;; after build, clojurescript/garden will be translated
                 ;; into assets, no need to include them in war file
                 [garden "1.3.2" :scope "cljs"]
                 [reagent "0.5.1" :scope "cljs"]
                 [cljs-http "0.1.38" :scope "cljs"]
                 [org.clojure/clojurescript "1.7.170" :scope "cljs"]
                 [org.clojure/core.async "0.2.374" :scope "cljs"]

                 [org.clojure/java.jdbc "0.4.2"]
                 [mysql/mysql-connector-java "5.1.37"]
                 [org.clojure/data.json "0.2.6"]
                 [ring/ring-json "0.4.0"]
                 [ring/ring-core "1.4.0"]
                 [ring-basic-authentication "1.0.5"]
                 [wechat "0.1.1"]
                 [hiccup "1.0.5"]
                 [compojure "1.4.0"]])

(require
 '[adzerk.boot-cljs :refer [cljs]]
 '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]]
 '[adzerk.boot-reload :refer [reload]]
 '[pandeiro.boot-http :refer [serve]]
 '[org.martinklepsch.boot-garden :refer [garden]]
 '[danielsz.autoprefixer :refer [autoprefixer]]
 '[cpmcdaniel.boot-copy :refer [copy]])

(deftask build []
  (comp (cljs)
     (garden :styles-var 'senna.styles/screen
             :output-to "main.css")
     (autoprefixer :files ["main.css"])
     (copy :output-dir "target/public/css/"
           :matching #{#"\.css$"})))

(deftask run []
  (comp (serve :handler 'senna.core/handler
            :reload true)
     (watch)
     (cljs-repl)
     (reload)
     (build)
     ))

(deftask production []
  (task-options! cljs {:optimizations :advanced}
                 garden {:pretty-print false})
  identity)

(deftask development []
  (task-options! cljs {:optimizations :none
                       :source-map true}
                 garden {:pretty-print true}
                 reload {:on-jsload 'senna.app/init
                         :asset-path "public"})
  identity)

(deftask dist []
  (comp (production)
     (build)
     (aot :namespace #{'senna.core
                       'senna.index
                       'senna.dashboard})
     (web :serve 'senna.core/handler)
     (uber :exclude-scope #{"provided" "boot" "cljs"})
     (jar :file "senna.jar")))

(deftask dev []
  (comp (development)
     (run)))
