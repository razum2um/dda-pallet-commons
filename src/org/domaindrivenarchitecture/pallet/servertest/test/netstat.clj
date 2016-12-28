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

(ns org.domaindrivenarchitecture.pallet.servertest.test.netstat
  (:require
    [schema.core :as s]
    [org.domaindrivenarchitecture.pallet.servertest.fact.netstat :as netstat-fact]
    [org.domaindrivenarchitecture.pallet.servertest.core.test :as server-test]))

(defn filter-listening-prog
  "filter for program ist listening."
  [prog port named-netastat-line]
  (and (= (:state named-netastat-line) "LISTEN")
       (= (:process-name named-netastat-line) prog)
       (re-matches 
         (re-pattern (str ".+:" port)) 
         (:local-address named-netastat-line))
       ))

(s/defn prog-listen? :- server-test/TestResult
  [prog :- s/Str 
   port :- s/Num
   input :- s/Any]
  (let [filter-result (filter 
                        #(filter-listening-prog prog port %)
                        input)
        passed (some? filter-result)
        summary (if passed "TEST PASSED" "TEST FAILED")]
    {:input input
     :test-passed passed
     :test-message (str "test for : " prog ", " port " summary: " summary)
     :summary summary}
   ))


(s/defn test-prog-listen :- server-test/TestActionResult 
  [prog :- s/Str 
   port :- s/Num]
  (server-test/test-it 
    netstat-fact/fact-id-netstat
    #(prog-listen? prog port %)))
