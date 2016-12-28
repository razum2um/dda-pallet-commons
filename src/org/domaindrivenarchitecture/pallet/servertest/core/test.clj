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

(ns org.domaindrivenarchitecture.pallet.servertest.core.test
  (:require
    [clojure.tools.logging :as logging]
    [schema.core :as s]
    [pallet.crate :as crate]
    [pallet.actions :as actions]))

(def TestResult
  {:input s/Any
   :test-passed s/Bool
   :test-message s/Str
   :summary s/Str})

(def TestActionResult
  {:context s/Str
   :action-symbol s/Any
   :input s/Any
   :out s/Str
   :exit s/Num 
   :summary s/Str})

(s/defn test-action-result :- TestActionResult
  [context :- s/Str
   fact-key :- s/Keyword
   fact-key-name :- s/Str
   test-result :- TestResult]
  (let [action-symbol (str "test-" fact-key-name)]
    {:context context
     :action-symbol action-symbol
     :input (-> test-result :input)
     :out (-> test-result :test-message)
     :exit 0
     :summary (-> test-result :summary)}
    ))

(s/defn test-it :- TestActionResult
  [fact-key :- s/Keyword
   test-fn]
  (let [all-facts (crate/get-settings :dda-servertest-fact {:instance-id (crate/target-node)})
        facts (-> all-facts fact-key)
        fact-key-name (name fact-key)]  
    (actions/as-action
      (logging/info (str "testing " fact-key-name))
      (let [input (:out @facts)
            context (:context @facts)
            test-result (apply test-fn (list input))
            action-result (test-action-result context fact-key fact-key-name test-result)]
        (logging/debug (str "input: " input))
        (logging/debug (str "result: " test-result))
        (logging/info (str "result for " fact-key-name " : " (-> action-result :summary)))
        action-result
      ))
    ))
