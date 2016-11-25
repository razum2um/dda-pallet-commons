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

(ns org.domaindrivenarchitecture.pallet.servertest.resources  
  (:require
    [pallet.core.session :as session]
    [clojure.tools.logging :as logging]
    [pallet.crate :as crate]
    [pallet.actions :as actions]
    [pallet.stevedore :refer :all]
    [pallet.script :as script]
    [pallet.script.lib :as lib]
    [org.domaindrivenarchitecture.pallet.servertest.scripts.core :refer :all]))

(defn- resource-data
  "Generates an internal representation of the resource and applies the 
   transform-fn if provided."
  [resource-key script out & {:keys [transform-fn]}]
  {:dda-test-resource true
   :resource-key resource-key
   :transformed-out (if (fn? transform-fn) (transform-fn out) out)
   :out out
   :script script})

(defn define-resource-from-script
  "Defines a resource as output from an arbitry script. This fails if the
   script fails (exitcode <> 0) or the resource file cannot be created.
   Side effects on target node:
     * copy the script to state folder on target node
     * execute script on target node and save result"
  {:pallet/plan-fn true}
  [resource-key script & {:keys [transform-fn]}]
 (actions/as-action
    (logging/info "got transform-fn" (str transform-fn) (fn? transform-fn)))
  ; create the script file in the state directory
  (actions/remote-file 
    (resource-script-path resource-key)
    :content script :owner "pallet" :group "pallet" :mode "700")
  ; create an empty file for the script resource
  (actions/file 
    (resource-file-path resource-key)
    :owner "pallet" :group "pallet" :mode "600")
  ; execute the script and save nv for transform output to settings
  (let [nv (actions/exec-script (script-run-resource ~resource-key))
        output-nv (actions/with-action-values [nv] 
                    (resource-data resource-key script (:out nv) :transform-fn transform-fn))]
    (crate/assoc-settings :dda-servertest-resources {resource-key output-nv})
    output-nv))

(defn define-resource-from-file
  "Defines a remote file as a resource. This fails if the file does not exist
   or the resource file cannot be created."
  [resource-key file & transform-fn]
  (let [script (script (lib/cat ~file))]
(define-resource-from-script resource-key script :transform-fn transform-fn)))