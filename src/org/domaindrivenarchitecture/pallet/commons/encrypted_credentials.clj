

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
   [dda.config.commons.directory-model :as dir]
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

(s/defn load-secret-keyring
  "Load the secret keyring from configured position."
  [encryption-config :- EncryptionConfiguration]
  (let [secring-path (secring-path encryption-config)]
    (keyring/load-secret-keyring 
      (if (.exists (io/file secring-path))
        (io/file secring-path)
        (io/file (io/resource secring-path))))))

(s/defn get-public-key
  "get the public key from given configuration."
  [encryption-config :- EncryptionConfiguration]
  (keyring/get-public-key
    (load-secret-keyring encryption-config)
    (get-in encryption-config [:key-id])))

(s/defn get-secret-key
  "get the private key from given configuration."
  [encryption-config :- EncryptionConfiguration]
  (keyring/get-secret-key
    (load-secret-keyring encryption-config)
    (get-in encryption-config [:key-id])))


(s/defn encrypt-secret
  "encrypt the secret. encryptor can be passphrase or public key."
  [encryptors :- [s/Str]
   secret :- s/Str]
  (pgp-msg/encrypt secret encryptors :format :utf8 :armor true))

(s/defn encrypt :- EncryptedCredential
  "encrypt the secret part of encryptable."
  [encryptors :- s/Str
   encryptable :- UnencryptedCredential]
  (assoc encryptable :secret 
         (encrypt-secret encryptors (get-in encryptable [:secret]))))

(s/defn decrypt-secret 
  "decrypts the secret. decryptor can be passphrase or private key. 
  alternatively you can provide a seckey as decryptor and the corresponding keyphrase for unlocking."
  [decryptor :- s/Str
   secret :- s/Str & keyphrase]
  (if (nil? keyphrase) 
  (pgp-msg/decrypt secret decryptor)
  (pgp-msg/decrypt secret (apply pgp/unlock-key decryptor keyphrase))
  ))

(s/defn decrypt :- UnencryptedCredential
  "decrypt the secret part of encryptable. for decryptor options see decrypt-secret."
  [decryptor :- s/Str
   decryptable :- EncryptedCredential & keyphrase ]
  (assoc decryptable :secret 
         (apply decrypt-secret decryptor (get-in decryptable [:secret]) keyphrase)))
