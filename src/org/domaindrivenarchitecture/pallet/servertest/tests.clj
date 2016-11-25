; Licensed to the Apache Software Foundation (ASF) under one
; or more contributor license agreements. See the NOTICE file
; distributed with this work for additional information
; regarding copyright ownership. The ASF licenses this file
; to you under the Apache License, Version 2.0 (the
; "License"); you may not use this file except in compliance
; with the License. You may obtain a copy of the License at
;
; http://www.apache.org/licenses/LICENSE-2.0
;
; Unless required by applicable law or agreed to in writing, software
; distributed under the License is distributed on an "AS IS" BASIS,
; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
; See the License for the specific language governing permissions and
; limitations under the License.

(ns org.domaindrivenarchitecture.pallet.servertest.tests
  (:require
    [pallet.crate :as crate]
    [pallet.actions :as actions]
    [pallet.stevedore :refer :all]
    [org.domaindrivenarchitecture.pallet.servertest.resources :refer :all]
    [org.domaindrivenarchitecture.pallet.servertest.scripts.core :refer :all]))

(defn- test-result-data
  "Creates an internal representation used for test results."
  [resource-key out exit]
  {:dda-test-result true
   :resource-key resource-key
   :out out
   :exit exit})

(defn testclj-resource
  "Performs a local clojure test for the given resource. The given test-fn receives
   the resource as argument. The given test-fn should:
     * evaluate to the test-result, which means
         (if (test-fn resource) TEST-PASSED TEST-FAILED)
     * output additional information to stdout (e.g. using println)
       if desired, this will show up in the test result

   Optional parameters:
     :use-transformed-out (true/false, defaults true)
         use the transformed or the raw output (=string) of the script."
  {:pallet/plan-fn true}
  [resource-key test-fn & {:keys [use-transformed-out] :or {use-transformed-out true}}]
  (actions/as-action
    (let [writer (new java.io.StringWriter)
          output-nv @(-> (crate/get-settings :dda-servertest-resources) resource-key)
          out (if use-transformed-out (-> output-nv :transformed-out) (-> output-nv :out))
          test-fn-result (binding [*out* writer] (test-fn out))]
      (test-result-data resource-key (str writer) test-fn-result))))

(defn testnode-resource
  "Runs a script that receives the resource in stdin. The provided script 
   should:
    * Exit with code 0 iff the test is passed and exit with any other
      code on failure. This is transformed to true/false after executing.
    * Provide a human-readable test result (like reasons for failure)
      on stdout."
  {:pallet/plan-fn true}
  [resource-key script]
  (let [nv (actions/exec-script (script-test-resource ~resource-key ~script))]
    (actions/with-action-values [nv] 
      (test-result-data 
        resource-key
        ; output: remove the exit-code line
        (clojure.string/join "\n" (butlast (clojure.string/split-lines (:out nv))))
        ; result: transform exit-code in last line to true/false
        (if (= 0(Integer. (last (clojure.string/split-lines (:out nv))))) true false))
      )))

(defn- test-result-node
  "Create test result for a single node."
  [test-phase-result]
  {:node (-> test-phase-result :target :node)
   :resources (filter #(:dda-test-resource %) (:result test-phase-result))
   :results (filter #(:dda-test-result %) (:result test-phase-result))}
  )

(defn test-result
  "Transform session to test result for all tested nodes."
  [session]
  (let [results (:results session)
        test-phase-results (filter #(= :test (:phase %)) results)]
    (map test-result-node test-phase-results)))

;;; Some predefined tests

(defn testnode-not-empty
  "Tests if a created resource is not-empty (=success) or is empty (=failure)"
  [resource-key & {:keys [strip] :or {strip false}}]
  (testnode-resource resource-key (script-test-not-empty :strip strip)))

(defn testnode-match-regex
  "Tests if a file matches a regular expression using grep."
  [resource-key regex]
  (testnode-resource resource-key (script-test-match-regexp regex)))

