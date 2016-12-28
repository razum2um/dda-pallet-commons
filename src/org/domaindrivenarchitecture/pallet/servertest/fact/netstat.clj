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

(ns org.domaindrivenarchitecture.pallet.servertest.fact.netstat
  (:require
    [org.domaindrivenarchitecture.pallet.servertest.core.fact :refer :all]))

(def fact-id-netstat ::netstat)

(defn parse-netstat
  [netstat-resource]
  (map #(zipmap 
          [:proto :recv-q :send-q :local-adress :foreign-adress :state :user :inode :pid :process-name]
          (clojure.string/split (clojure.string/trim %) #"\s+|/"))
     (drop-while #(not (re-matches #"\s*(tcp|udp).*" %)) 
       (clojure.string/split netstat-resource #"\n"))))

(defn collect-netstat-fact
  "Defines the netstat resource. 
   This is automatically done serverstate crate is used."
  []
  (collect-fact fact-id-netstat '("netstat" "-tulpen") :transform-fn parse-netstat))