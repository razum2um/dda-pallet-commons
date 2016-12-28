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

(ns org.domaindrivenarchitecture.pallet.servertest.fact.packages
  (:require
    [org.domaindrivenarchitecture.pallet.servertest.core.fact :refer :all]))

(def fact-id-packages ::packages)

(defn parse-packages
  [packages-fact]
  (map #(zipmap [:state :package :version :arch :desc]
              (clojure.string/split % #"\s+|/"))
       (drop-while #(re-matches #"\s*(Desired|\||\+).*" %) 
                   (clojure.string/split packages-fact #"\n")))
  )

(defn collect-packages-fact
  "Defines the netstat resource. 
   This is automatically done serverstate crate is used."
  []
  (collect-fact fact-id-packages '("dpkg" "-l") :transform-fn parse-packages))
