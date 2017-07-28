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


(ns dda.pallet.commons.plan-test-utils
  (:require
    [schema.core :as s]))

(def Meta
  "commands meta data"
  {:language :bash
   (s/optional-key :summary) s/Str})

(def Command
  "command as it's executed ontarget node."
  s/Str)

(def Action
  "command as it's executed ontarget node."
  [Meta Command])

(def NodeValue
  "a value bound to a target node"
  {s/Symbol Action})

(defn extract-node-values
  "extract node values from a plan"
  [plan]
  (let [files-to-transfer (nth plan 0)
        session (nth plan 1)
        node-values (-> session :plan-state :node-values)]
    node-values))

(defn extract-actions
  "extract actions from node values"
  [node-values]
  (vals node-values))

(defn extract-actions-meta
  "extract the actions metadata"
  [actions]
  (map (fn [x] (:summary (first x))) actions))

(defn extract-action-summary-containing
  "extract action metas containig the specified string"
  [containing actions-meta]
  (filter (fn [y] (and (some? y) (.contains y containing)))
          actions-meta))

  (defn extract-nth-action-command
    "extract the command from nth action"
    [plan action-number]
    (let [node-values (extract-node-values plan)
          action ((nth (keys node-values) action-number) node-values)
          command (nth action 1)]
      command))