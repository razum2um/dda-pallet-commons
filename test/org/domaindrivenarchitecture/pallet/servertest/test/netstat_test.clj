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


(ns org.domaindrivenarchitecture.pallet.servertest.test.netstat-test
  (:require
    [clojure.test :refer :all]
    [pallet.build-actions :as build-actions]
    [pallet.actions :as actions]
    [org.domaindrivenarchitecture.pallet.servertest.test.netstat :as sut]
    ))

(def named-netastat-line
  {:foreign-address ":::*",
   :local-address ":::80",
   :recv-q "0",
   :inode "44161",
   :state "LISTEN",
   :process-name "apache2",
   :proto "tcp6",
   :pid "4135",
   :send-q "0",
   :user "0"})

(deftest test-filter-for-listening-prog
  (testing 
    "test filtering for listening prog" 
      (is (sut/filter-listening-prog 
            "apache2"
            80
            named-netastat-line))
      (is (not (sut/filter-listening-prog 
                 "sshd"
                 80
                 named-netastat-line)))
      (is (not (sut/filter-listening-prog 
                 "apache2"
                 81
                 named-netastat-line)))
      ))


(deftest test-port
  (testing 
    "test the listen port test" 
      (is (sut/prog-listen? "apache2" 80 [named-netastat-line]))
      ))