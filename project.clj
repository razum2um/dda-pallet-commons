(defproject org.domaindrivenarchitecture/dda-pallet-commons "0.1.1-SNAPSHOT"
  :description "common utils for dda pallet"
  :url "https://www.domaindrivenarchitecture.org"
  :license {:name "Apache License, Version 2.0"
            :url "https://www.apache.org/licenses/LICENSE-2.0.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [prismatic/schema "1.0.5"]
                 [metosin/schema-tools "0.7.0"]
                 [com.palletops/pallet "0.8.11"]]
  :repositories [["snapshots" :clojars]
                 ["releases" :clojars]]
  :deploy-repositories [["snapshots" :clojars]
                        ["releases" :clojars]]
  :profiles {:dev
             {:dependencies
              [[org.clojure/test.check "0.9.0"]
               [com.palletops/pallet "0.8.11" :classifier "tests"]]}
             :leiningen/reply
             {:dependencies [[org.slf4j/jcl-over-slf4j "1.7.2"]]
              :exclusions [commons-logging]}}
  :local-repo-classpath true
  :classifiers {:tests {:source-paths ^:replace ["test"]
                        :resource-paths ^:replace []}})