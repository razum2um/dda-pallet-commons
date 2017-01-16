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
(ns org.domaindrivenarchitecture.pallet.servertest.test.packages
  (:require
    [schema.core :as s]
    [org.domaindrivenarchitecture.pallet.servertest.core.test :as server-test]
    [org.domaindrivenarchitecture.pallet.servertest.fact.packages :as packages-fact]))

(defn filter-installed-package
  "filter for installed packages."
  [package parsed-package-line]
  (= (:package parsed-package-line) package)
  )

(s/defn installed? :- server-test/TestResult
  [package :- s/Str 
   input :- s/Any]
  (let [filter-result (filter 
                        #(filter-installed-package package %)
                        input) 
        passed (not (empty? filter-result))
        summary (if passed "TEST PASSED" "TEST FAILED")]
    {:input input
     :test-passed passed
     :test-message (str "test for : " package " summary: " summary)
     :summary summary}
    ))

(s/defn test-installed? :- server-test/TestActionResult 
  [package :- s/Str]
  (server-test/test-it 
    packages-fact/fact-id-packages
    #(installed? package %))
  )
