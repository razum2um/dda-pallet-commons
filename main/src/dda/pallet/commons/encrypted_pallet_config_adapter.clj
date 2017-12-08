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

(ns dda.pallet.commons.encrypted-pallet-config-adapter
  (:require
   [schema.core :as s]
   [pallet.api :as api]
   [dda.pallet.commons.encrypted-credentials :as crypto]))

(s/defn get-pallet-credentials :- crypto/UnencryptedCredential
  ([path :- [s/Keyword]]
   (let [pallet-config (pallet.configure/pallet-config)]
     (get-in pallet-config path)))
  ([path :- [s/Keyword]
    key-id :- s/Str
    key-passphrase :- s/Str]
   (let [pallet-config (pallet.configure/pallet-config)
         desired-config (get-in pallet-config path)]
     (if (crypto/encrypted? desired-config)
       (crypto/decrypt
         (crypto/get-secret-key
           {:user-home (str (System/getenv "HOME") "/")
            :key-id key-id})
         desired-config
         key-passphrase)
       desired-config))))
