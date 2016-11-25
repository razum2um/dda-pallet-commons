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
    [org.domaindrivenarchitecture.pallet.servertest.tests :as tests]
    [org.domaindrivenarchitecture.pallet.servertest.resource.netstat :as netstat-res]
    [org.domaindrivenarchitecture.pallet.servertest.scripts.core :refer :all]))

(defn parse-netstat
  [netstat-resource]
  (map #(zipmap [:proto :recv-q :send-q :local-adress :foreign-adress :state :user :inode :pid :process-name]
              (clojure.string/split % #"\s+|/"))
     (rest netstat-resource)))

(defn filter-listening-prog
  "filter for program ist listening."
  [prog port named-netastat-line]
  (and (= (:state named-netastat-line) "LISTEN")
       (= (:process-name named-netastat-line) prog)
       (re-matches 
         (re-pattern (str ".+:" port)) 
         (:local-address named-netastat-line))
       ))

(defn prog-listen?
  [prog port netstat-resource]
  (some? (filter 
           #(filter-listening-prog prog port %)
           (parse-netstat netstat-resource))))

(defn test-process-listen?
  [prog port]
  (tests/testclj-resource 
      netstat-res/res-id-netstat
      (partial prog-listen? prog port)))