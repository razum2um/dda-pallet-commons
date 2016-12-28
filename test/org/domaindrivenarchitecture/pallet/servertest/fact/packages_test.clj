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


(ns org.domaindrivenarchitecture.pallet.servertest.fact.packages-test
  (:require
    [clojure.test :refer :all]
    [pallet.build-actions :as build-actions]
    [pallet.actions :as actions]
    [org.domaindrivenarchitecture.pallet.servertest.fact.packages :as sut]
    ))

(def packages-resource "Desired=Unknown/Install/Remove/Purge/Hold
| Status=Not/Inst/Conf-files/Unpacked/halF-conf/Half-inst/trig-aWait/Trig-pend
|/ Err?=(none)/Reinst-required (Status,Err: uppercase=bad)
||/ Name                                  Version                                  Architecture Description
+++-=====================================-========================================-============-===============================================================================
ii  accountsservice                       0.6.40-2ubuntu11.3                       amd64        query and manipulate user account information
ii  acl                                   2.2.52-3                                 amd64        Access control list utilities
ii  acpid                                 1:2.0.26-1ubuntu2                        amd64        Advanced Configuration and Power Interface event daemon
ii  adduser                               3.113+nmu3ubuntu4                        all          add and remove users and groups
")


(deftest test-parse
  (testing 
    "test parsing packages-output" 
      (is (= "accountsservice"
             (:package
               (first (sut/parse-packages packages-resource)))))
      ))
