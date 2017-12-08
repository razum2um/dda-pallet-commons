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
(ns dda.pallet.commons.encrypted-credentials-test
  (:require
    [clojure.java.io :as io]
    [clojure.test :refer :all]
    [clojure.test.check.generators :as gen]
    [schema.core :as s]
    [byte-streams :refer [bytes=]]
    [clj-pgp.core :as core]
    [clj-pgp.generate :as pgp-gen]
    [clj-pgp.test.encryption-test-scenario :as test-scenario]
    [dda.pallet.commons.encrypted-credentials :as sut]))


(def encryptable-credential 
 {:account "account identifier unencrypted" 
  :secret "ascii armored & gpg encrypted"})

(deftest encryptd?-test
 (testing 
   (is (sut/unencrypted? nil))
   (is (sut/unencrypted? "xx"))
   (is (sut/encrypted? "-----BEGIN PGP MESSAGE----- xx"))
   ))

(deftest encrypt-secret-test
 (testing 
   (is 
     (sut/encrypted?
       (sut/encrypt-secret test-scenario/pubkey "nobody can read this")))
   ))

(deftest encrypt-test
 (testing 
   (is (s/validate sut/EncryptedCredential
                   (sut/encrypt test-scenario/pubkey encryptable-credential)))
     ))

(deftest decrypt-secret-test
 (testing 
   (is 
     (sut/unencrypted?
       (sut/decrypt-secret test-scenario/privkey (sut/encrypt-secret test-scenario/pubkey "nobody can read this"))))
   (is 
     (sut/unencrypted?
       (sut/decrypt-secret
         (sut/get-secret-key
              {:user-home "/home/user/"
              :secring-path "clj_pgp/test/keys/secring.gpg"
              :key-id "3f40edec41c6cb7d"})
         (sut/encrypt-secret test-scenario/pubkey "nobody can read this")
         "test password")))
   ))

(deftest decrypt-test
 (testing 
   (is (s/validate sut/UnencryptedCredential
                   (sut/decrypt test-scenario/privkey (sut/encrypt test-scenario/pubkey encryptable-credential))))
     ))

(deftest secring-path-test
  (testing
    (is (= "/var/lib/pallet/secring.gpg"
           (sut/secring-path {:pallet-home "/var/lib/pallet/"
                              :user-home "/home/user/"})))
    (is (= "/home/user/.gnupg/secring.gpg"
           (sut/secring-path {:user-home "/home/user/"})))
    (is (= "/a/path.gpg"
           (sut/secring-path {:pallet-home "/var/lib/pallet/"
                              :user-home "/home/user/"
                              :secring-path "/a/path.gpg"})))
    ))

(deftest secret-key-ring-test
  (testing
    (is (= 2
           (count
                (clj-pgp.keyring/list-public-keys 
                  (sut/load-secret-keyring {:pallet-home "/var/lib/pallet/"
                      :user-home "/home/user/"
                      :secring-path "clj_pgp/test/keys/secring.gpg"})))))
    (is (= 4557904421870553981
          (core/key-id 
            (sut/get-public-key {:user-home "/home/user/"
              :secring-path "clj_pgp/test/keys/secring.gpg"
              :key-id "3f40edec41c6cb7d"}))))
    (is (= 4557904421870553981
          (core/key-id 
            (sut/get-secret-key
              {:user-home "/home/user/"
              :secring-path "clj_pgp/test/keys/secring.gpg"
              :key-id "3f40edec41c6cb7d"}))))
    ))

(deftest schema-test
 (testing 
     (is (s/validate sut/EncryptableCredential encryptable-credential))
     (is (s/validate sut/EncryptionConfiguration (sut/default-encryption-configuration)))
     )) 