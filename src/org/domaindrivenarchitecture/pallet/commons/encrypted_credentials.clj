

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
    [schema.core :as s]
    [clj-pgp.core :as pgp]
    [clj-pgp.keyring :as keyring]
    [clj-pgp.message :as pgp-msg]
    [org.domaindrivenarchitecture.config.commons.directory-model :as dir]
 ))

(s/defn encrypted? :- s/Bool
  [message :- s/Str]
  (if (empty? message)
    false
    (.startsWith message "-----BEGIN PGP MESSAGE-----" 0)))

(s/defn unencrypted? :- s/Bool
  [message :- s/Str]
  (not (encrypted? message)))

(def EncryptableCredential 
  {:account s/Str
   :secret s/Str})

(def UnencryptedCredential 
  {:account s/Str
   :secret (s/pred unencrypted?)})

(def EncryptedCredential 
  {:account s/Str
   :secret (s/pred encrypted?)})

(def EncryptionConfiguration 
  {:user-home dir/NonRootDirectory
   (s/optional-key :pallet-home) dir/NonRootDirectory
   (s/optional-key :secring-path) s/Str
   (s/optional-key :key-id) s/Str})

(defn default-encryption-configuration []
  (let [user-home (get (System/getenv) "HOME" "~")
        pallet-home (get (System/getenv) "PALLET_HOME")]
  (merge
    {:user-home (str user-home "/")}
    (if (some? pallet-home) 
      {:pallet-home (str pallet-home "/")}
      {}))
  ))

(s/defn secring-path :- s/Str
  [encryption-config :- EncryptionConfiguration]
  (cond 
    (contains? encryption-config :secring-path) (get-in encryption-config [:secring-path])
    (contains? encryption-config :pallet-home) (str (get-in encryption-config [:pallet-home]) "secring.gpg")
    :else (str (get-in encryption-config [:user-home]) ".gnupg/secring.gpg")
    ))


(s/defn encrypt-secret
  "encrypt the secret."
  [public-key 
   secret :- s/Str]
  (pgp-msg/encrypt secret public-key :armor true))

(s/defn encrypt :- EncryptedCredential
  "encrypt the secret part of encryptable."
  [pubkey :- s/Str
   encryptable :- UnencryptedCredential]
  (assoc encryptable :secret 
         (encrypt-secret pubkey (get-in encryptable [:secret]))))

(defn load-secret-keyring [custom-location])

(def keyring (keyring/load-secret-keyring (io/file "/home/mje/.gnupg/secring.gpg")))

