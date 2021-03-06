(defproject app "0.1.0-SNAPSHOT"
  :dependencies
  [[org.clojure/clojure "1.10.1"]
   [org.clojure/clojurescript "1.10.773"]
   [com.stuartsierra/component "1.0.0"]

   ;; html
   [selmer "1.12.31" :exclusions [commons-codec]]

   ;; cljs
   [reagent "0.10.0" ]
   [re-frame "1.1.1" ]
   [cljs-ajax "0.8.1" ]

   ;; basic web setup
   [compojure "1.6.2" :exclusions [commons-codec]]
   [org.immutant/web "2.1.10"
    :exclusions [commons-codec]]
   [lib-noir "0.9.9" :exclusions [commons-codec]]
   [ring/ring-defaults "0.3.2" :exclusions [commons-codec]]
   [ring "1.8.2" :exclusions [commons-codec]]
   ;;   [com.taoensso/carmine "3.0.1" :exclusions [com.taoensso/encore]]

   ;; standard web utilities
   [com.taoensso/timbre "5.1.0"]
   [environ "1.2.0"]

   ;; file/formatting and development utilities
   [org.clojure/tools.namespace "1.0.0"]
   [dk.ative/docjure "1.14.0"]
   [danlentz/clj-uuid "0.1.9"]
   [me.raynes/fs "1.4.6"]
   [prismatic/schema "1.1.12"]
   [pjstadig/humane-test-output "0.10.0"]
   [ring/ring-mock "0.4.0"]]

  :injections [(require 'pjstadig.humane-test-output)
               (pjstadig.humane-test-output/activate!)]

  :min-lein-version "2.5.3"

  :source-paths ["src/clj" "dev"]

  :resource-paths ["resources"]

  :repl-options {:init-ns user}

  :plugins [[lein-cljsbuild "1.1.8"]
            [lein-cloverage "1.2.1"]]

  :cljsbuild
  {:test-commands
   {"desktop-test" ["phantomjs"
                    "resources/public/js/app-desktop-test.js"
                    "http://localhost:4000"]}
   :builds
   [{:id           "desktop"
     :source-paths ["src/cljs"]
     :compiler     {:main            app.core
                    :output-dir      "resources/public/js/compiled/out-viewer"
                    :output-to       "resources/public/js/viewer.js"
                    :closure-defines {"goog.DEBUG" false}
                    :optimizations   :advanced
                    :pretty-print    false}}
    {:id           "desktop-test"
     :source-paths ["src/cljs" "test/cljs/app"]
     :compiler     {:main            alfa.runner
                    :output-to       "resources/public/js/app-desktop-test.js"
                    :optimizations   :whitespace}}]}

  :profiles {}
  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]
  :figwheel {:css-dirs ["resources/public/vendors"]})
