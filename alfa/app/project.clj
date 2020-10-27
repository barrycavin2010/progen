(defproject alfa "0.1.0-SNAPSHOT"
  :dependencies
  [[org.clojure/clojure "1.8.0"]
   [org.clojure/clojurescript "1.9.93"]
   [com.stuartsierra/component "0.3.1"]

   ;; html
   [selmer "1.0.7" :exclusions [commons-codec]]

   ;; cljs
   [reagent "0.6.0-alpha2" :exclusions [org.clojure/tools.reader cljsjs/react]]
   [re-frame "0.7.0" :exclusions [reagent cljsjs/react]]
   [cljs-ajax "0.5.8" ]
   [cljs-react-material-ui "0.2.19" ]
   ;;:exclusions [cljsjs/react]
   ;;[cljsjs/react-with-addons "15.1.0-0"]

   ;; basic web setup
   [compojure "1.5.1" :exclusions [commons-codec]]
   [org.immutant/web "2.1.5"
    :exclusions [commons-codec]]
   [lib-noir "0.9.9" :exclusions [commons-codec]]
   [ring/ring-defaults "0.2.1" :exclusions [commons-codec]]
   [ring "1.5.0" :exclusions [commons-codec]]
   [com.taoensso/carmine "2.13.1" :exclusions [com.taoensso/encore]]

   ;; standard web utilities
   [com.taoensso/timbre "4.6.0"]
   [environ "1.0.3"]

   ;; file/formatting and development utilities
   [org.clojure/tools.namespace "0.2.11"]
   [dk.ative/docjure "1.10.0"]
   [danlentz/clj-uuid "0.1.6"]
   [me.raynes/fs "1.4.6"]
   [prismatic/schema "1.1.2"]
   [pjstadig/humane-test-output "0.8.0"]
   [ring/ring-mock "0.3.0"]]

  :injections [(require 'pjstadig.humane-test-output)
               (pjstadig.humane-test-output/activate!)]

  :min-lein-version "2.5.3"

  :source-paths ["src/clj" "dev"]

  :resource-paths ["resources"]

  :main alfa.core

  :repl-options {:init-ns user}

  :plugins [[lein-cljsbuild "1.1.3"]
            [lein-cloverage "1.0.7-SNAPSHOT"]]

  :cljsbuild
  {:test-commands
   {"desktop-test" ["phantomjs"
                    "resources/public/js/app-desktop-test.js"
                    "http://localhost:4000"]}
   :builds
   [{:id           "desktop"
     :source-paths ["src/cljs"]
     :compiler     {:main            alfa.core
                    :output-dir      "resources/public/js/compiled/out-desktop"
                    :output-to       "resources/public/js/app-desktop.js"
                    :closure-defines {"goog.DEBUG" false}
                    :optimizations   :whitespace
                    :pretty-print    false}}
    {:id           "desktop-test"
     :source-paths ["src/cljs" "test/cljs/alfa"]
     :compiler     {:main            alfa.runner
                    :output-to       "resources/public/js/app-desktop-test.js"
                    :optimizations   :whitespace}}]}

  :profiles {}
  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]
  :figwheel {:css-dirs ["resources/public/vendors"]})
