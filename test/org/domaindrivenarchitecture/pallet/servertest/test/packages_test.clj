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


(ns org.domaindrivenarchitecture.pallet.servertest.test.packages-test
  (:require
    [clojure.test :refer :all]
    [pallet.build-actions :as build-actions]
    [pallet.actions :as actions]
    [org.domaindrivenarchitecture.pallet.servertest.test.packages :as sut]
    ))

(def named-packages-line
  {:state "ii"
   :package "accountsservice"
   :version "0.6.40-2ubuntu11.3"
   :arch "amd64"
   :desc "query and manipulate user account information"})

(deftest test-filter-installed
  (testing 
    "test for installed in one single line" 
      (is (sut/filter-installed-package "accountsservice" named-packages-line))
      (is (not (sut/filter-installed-package "adduser" named-packages-line)))
      ))

(deftest test-installed
  (testing 
    "test for installed packages" 
      (is (= "TEST FAILED" (:summary (sut/installed? "query" [named-packages-line]))))
      (is (= "TEST FAILED" (:summary (sut/installed? "account" [named-packages-line]))))
      (is (= "TEST PASSED" (:summary (sut/installed? "accountsservice" [named-packages-line]))))
      ))