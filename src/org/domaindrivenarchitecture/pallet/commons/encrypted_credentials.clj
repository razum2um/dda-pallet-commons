

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

(ns org.domaindrivenarchitecture.pallet.commons.encrypted-credentials
  (:require
    [clojure.java.io :as io]
    [clj-pgp.core :as pgp]
    [clj-pgp.keyring :as keyring]
    [clj-pgp.message :as pgp-msg]
 ))

(def schema 

(def keyring (keyring/load-secret-keyring (io/file "/home/mje/.gnupg/secring.gpg")))

(def pubkey (second (keyring/list-public-keys keyring)))

(def seckey (keyring/get-secret-key keyring (pgp/hex-id pubkey)))

(def message
     (pgp-msg/encrypt
       "MeinTest" pubkey
       :format :utf8
       :cipher :aes-256
       :compress :zip
       :armor true))