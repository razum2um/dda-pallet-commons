(defproject org.domaindrivenarchitecture/dda-pallet-commons "0.3.0"
  :description "common utils for dda pallet"
  :url "https://www.domaindrivenarchitecture.org"
  :license {:name "Apache License, Version 2.0"
            :url "https://www.apache.org/licenses/LICENSE-2.0.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [prismatic/schema "1.1.3"]
                 [metosin/schema-tools "0.9.0"]
                 [mvxcvi/clj-pgp "0.8.3"]
                 [com.palletops/pallet "0.8.12"]
                 [org.domaindrivenarchitecture/dda-config-commons "0.1.7"]
                 [ch.qos.logback/logback-classic "1.1.7"]]
  :source-paths ["src" "test-utils"]
  :repositories [["snapshots" :clojars]
                 ["releases" :clojars]]
  :deploy-repositories [["snapshots" :clojars]
                        ["releases" :clojars]]
  :profiles {:dev
             {:dependencies
              [[org.clojure/test.check "0.9.0"]
               [com.palletops/stevedore "0.8.0-beta.7"]
               [com.palletops/pallet "0.8.12" :classifier "tests"]
               ;[mvxcvi/clj-pgp "0.9.0-SNAPSHOT" :classifier "tests"]
               ]}
             :plugins [[lein-sub "0.3.0"]]
             :leiningen/reply
             {:dependencies [[org.slf4j/jcl-over-slf4j "1.7.21"]]
              :exclusions [commons-logging]}}
  :local-repo-classpath true
  :classifiers {:tests {:source-paths ^:replace ["test"]
                        :resource-paths ^:replace []}})