(defproject dda/dda-pallet-commons "1.4.1-SNAPSHOT"
  :description "common utils for dda pallet"
  :url "https://www.domaindrivenarchitecture.org"
  :license {:name "Apache License, Version 2.0"
            :url "https://www.apache.org/licenses/LICENSE-2.0.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/tools.cli "0.3.7"]
                 [prismatic/schema "1.1.9"]
                 [mvxcvi/clj-pgp "0.9.0"]
                 [dda/dda-config-commons "1.4.0"]
                 [ch.qos.logback/logback-classic "1.3.0-alpha4"]
                 [dda/pallet "0.9.0"]
                 [keypin "0.7.4"]]
  :source-paths ["main/src"]
  :resource-paths ["main/resources"]
  :repositories [["snapshots" :clojars]
                 ["releases" :clojars]]
  :deploy-repositories [["snapshots" :clojars]
                        ["releases" :clojars]]
  :profiles {:dev {:source-paths ["integration/src"
                                  "test/src"
                                  "uberjar/src"]
                   :resource-paths ["integration/resources"
                                    "test/resources"]
                   :dependencies
                   [[org.clojure/test.check "0.10.0-alpha2"]
                    [dda/pallet "0.9.0" :classifier "tests"]
                    [ch.qos.logback/logback-classic "1.3.0-alpha4"]
                    [org.slf4j/jcl-over-slf4j "1.8.0-beta1"]]
                   :plugins
                   [[lein-sub "0.3.0"]]
                   :leiningen/reply
                   {:dependencies [[org.slf4j/jcl-over-slf4j "1.8.0-beta0"]]
                    :exclusions [commons-logging]}}
             :test {:test-paths ["test/src"]
                    :resource-paths ["test/resources"]
                    :dependencies [[dda/pallet "0.9.0" :classifier "tests"]]}
             :uberjar {:source-paths ["uberjar/src"]
                       :resource-paths ["uberjar/resources"]
                       :aot :all
                       :main dda.pallet.dda-managed-ide.main
                       :dependencies [[org.clojure/tools.cli "0.3.7"]]}}
  :classifiers {:tests :test}
  :local-repo-classpath true)
