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

(ns dda.pallet.commons.fact
  (:require
    [clojure.tools.logging :as logging]
    [schema.core :as s]
    [pallet.crate :as crate]
    [pallet.actions :as actions]))

(def ScriptResult
  {:context s/Str
   :out s/Any
   :action-symbol s/Any
   :exit s/Num
   :script s/Any})

(def FactResult
  {:context s/Str
   :action-symbol s/Any
   :script s/Any
   :out s/Any
   :out-raw  s/Str
   :exit s/Num
   :summary s/Str})

(s/defn fact-result :- FactResult
  [script-result :- ScriptResult
   context :- s/Str
   transform-fn]
  (let [out-raw (:out script-result)
        context context
        action-symbol (:action-symbol script-result)
        exit (:exit script-result)
        script (:script script-result)]
    {:context context
     :action-symbol action-symbol
     :script script
     :out (if (some? transform-fn)
            (apply transform-fn (list out-raw))
            out-raw)
     :out-raw  out-raw
     :exit exit
     :summary (if (= 0 exit) "SUCCESSFUL" "ERROR")}))


(s/defn collect-fact
  "Gets a fact from target node based on output of script.
   Exitcode <> 0 means fact is collected successfull.
   By convention the given script has no side effects on target system.

   `fact-key`
   should be a keyword like :netstat

   `script`
   should be a sequence like '(\"netsat\" \"-tulpen\")

   `transform-fn`
   should be a fn transforming script output to a nested map."

  {:pallet/plan-fn true}
  [facility :- s/Keyword
   fact-key :- s/Keyword
   script
   & options]
  (let [{:keys [transform-fn]
         :or {transform-fn nil}} options
        script-result (actions/exec-script ~script)
        fact-action-result (actions/as-action
                             (logging/info "transforming fact script result")
                             (logging/debug "script result: " script-result)
                             (let
                               [fact-result (fact-result
                                              @script-result
                                              (str "fact: " (name facility) "/" (name fact-key))
                                              transform-fn)]
                               (logging/debug "fact result: " fact-result)
                               fact-result))]
    (crate/assoc-settings
         facility {fact-key fact-action-result} {:instance-id (crate/target-node)})))
