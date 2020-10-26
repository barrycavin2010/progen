(defproject app "0.1.0-SNAPSHOT"
  :dependencies
  [[org.clojure/clojure "1.10.1"]
   [org.clojure/clojurescript "1.10.597"]
   [com.stuartsierra/component "0.4.0"]

   ;; html
   [selmer "1.12.18"
    ;; :exclusions [commons-codec]
    ]

   ;; cljs
   ;; [reagent "0.8.1" :exclusions [org.clojure/tools.reader cljsjs/react]]
   ;; [re-frame "0.10.9" :exclusions [reagent cljsjs/react]]
   ;; [cljs-ajax "0.8.0" ]
   ;; [cljs-react-material-ui "0.2.50" ]
   ;;:exclusions [cljsjs/react]
   ;;[cljsjs/react-with-addons "15.1.0-0"]

   ;; basic web setup
   ;; [compojure "1.6.1" :exclusions [commons-codec]]
   ;; [org.immutant/web "2.1.10" :exclusions [commons-codec]]
   ;; [lib-noir "0.9.9" :exclusions [commons-codec]]
   ;; [ring/ring-defaults "0.3.2" :exclusions [commons-codec]]
   ;; [ring "1.8.0" :exclusions [commons-codec]]
   [com.taoensso/carmine "2.19.1" :exclusions [com.taoensso/encore]]

   ;; standard web utilities
   ;; [com.taoensso/timbre "4.10.0"]
   ;; [environ "1.1.0"]

   ;; file/formatting and development utilities
   [org.clojure/tools.namespace "0.3.1"]
   [dk.ative/docjure "1.13.0"]
   [danlentz/clj-uuid "0.1.9"]
   [me.raynes/fs "1.4.6"]
   [prismatic/schema "1.1.12"]
   [pjstadig/humane-test-output "0.10.0"]
   ;; [ring/ring-mock "0.4.0"]
   ]

  :injections [(require 'pjstadig.humane-test-output)
               (pjstadig.humane-test-output/activate!)]

  :min-lein-version "2.5.3"

  :source-paths ["src/clj" "dev"]

  :resource-paths ["resources"]

  :repl-options {:init-ns user}

  :plugins [
            ;; [lein-cljsbuild "1.1.7"]
            ;; [lein-cloverage "1.1.2"]
            ]

  :profiles {}
  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]
  :figwheel {:css-dirs ["resources/public/vendors"]})