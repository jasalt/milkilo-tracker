(defproject
  milkilo-tracker
  "0.1.0-SNAPSHOT"
  :description
  "FIXME: write description"
  :url
  "http://example.com/FIXME"
  :dependencies
  [
   ;; Backend libs
   [ragtime "0.3.8"]
   [prone "0.8.0"]
   [log4j
    "1.2.17"
    :exclusions
    [javax.mail/mail
     javax.jms/jms
     com.sun.jdmk/jmxtools
     com.sun.jmx/jmxri]]
   [selmer "0.8.0"]
   [com.taoensso/tower "3.0.2"]
   [markdown-clj "0.9.62" :exclusions [com.keminglabs/cljx]]
   [im.chit/cronj "1.4.3"]
   [com.taoensso/timbre "3.3.1"]
   [org.postgresql/postgresql "9.3-1102-jdbc41"]
   [noir-exception "0.2.3"]
   [korma "0.4.0"]
   [lib-noir "0.9.5"]
   [org.clojure/clojure "1.6.0"]
   [clj-time "0.9.0"]
   [environ "1.0.0"]
   [ring-server "0.4.0"]
   [org.clojure/core.cache "0.6.4"]

   ;; Frontend libs
   [org.clojure/clojurescript "0.0-2850"]
   [figwheel "0.2.5"]
   [cljs-ajax "0.3.9"]
   [secretary "1.2.3-SNAPSHOT"]
   [com.cemerick/friend "0.2.1"]
   [reagent-forms "0.4.3"]
   [com.andrewmcveigh/cljs-time "0.3.2"]
   ]
  :repl-options
  {:init-ns milkilo-tracker.repl
   :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
  :jvm-opts
  ["-server"]
  :plugins
  [[lein-ring "0.9.0"]
   [lein-environ "1.0.0"]
   [lein-ancient "0.6.2"]
   [lein-cljsbuild "1.0.5"]
   [lein-figwheel "0.2.5"]
   [com.cemerick/clojurescript.test "0.3.2"]
   [ragtime/ragtime.lein "0.3.6"]]
  :ring
  {:handler milkilo-tracker.handler/app,
   :init milkilo-tracker.handler/init,
   :destroy milkilo-tracker.handler/destroy,
   :uberwar-name "milkilo-tracker.war"}
  :profiles
  {:uberjar
   {:cljsbuild
    {:jar true,
     :builds
     {:app
      {:source-paths ["env/prod/cljs"],
       :compiler {:optimizations :advanced, :pretty-print false}}}},
    :hooks [leiningen.cljsbuild],
    :omit-source true,
    :env {:production true},
    :aot :all},
   :production
   {:ring
    {:open-browser? false, :stacktraces? false, :auto-reload? false}},
   :dev
   {:cljsbuild {:builds {:app {:source-paths ["env/dev/cljs"]}}},
    :dependencies
    [[ring-mock "0.1.5"]
     [ring/ring-devel "1.3.2"]
     [pjstadig/humane-test-output "0.6.0"]],
    :injections
    [(require 'pjstadig.humane-test-output)
     (pjstadig.humane-test-output/activate!)],
    :env {:dev true}}}
  :ragtime
  {:migrations ragtime.sql.files/migrations,
   :database
   "jdbc:postgresql://localhost/milkilo?user=dbadmin&password=dbpass"}
  :cljsbuild
  {:builds
   {:app
    {:source-paths ["src-cljs"],
     :compiler
     {:output-dir "resources/public/js/out",
      :externs ["react/externs/react.js"],
      :optimizations :none,
      :output-to "resources/public/js/app.js",
      :source-map true,
      :pretty-print true}}}}
  :uberjar-name
  "milkilo-tracker.jar"
  :min-lein-version "2.0.0"
  :figwheel
  {
   :nrepl-port 7888
   :http-server-root "public" ;; this will be in resources/
   :server-port 3449
   :css-dirs ["resources/public/css"]
   :open-file-command "file-opener"
   :repl true
   }
  :clean-targets ^{:protect false}
  ["resources/public/js/out/"
   "resources/phantom/out/"]
  )
