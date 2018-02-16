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
(ns dda.pallet.commons.session
  (:require
    [schema.core :as s]
    [dda.pallet.commons.session-target :as st]
    [dda.pallet.commons.session-result :as sr]))

(def SessionSpec
  {:new-nodes s/Any
   :old-nodes s/Any
   :targets s/Any
   :plan-state s/Any
   :service-state s/Any
   :results [{:target st/TargetSpec
              :target-type s/Any
              :plan-state s/Any
              :result '(sr/ResultSpec)
              :phase s/Any}]
   :environment s/Any
   :initial-plan-state s/Any})
