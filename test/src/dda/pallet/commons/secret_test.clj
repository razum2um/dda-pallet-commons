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


(ns dda.pallet.commons.secret-test
  (:require
    [clojure.test :refer :all]
    [schema.core :as s]
    [dda.pallet.commons.secret :as sut]))

(def secret
  {:plain "test"})

(deftest test-schema
  (testing
    (is (s/validate sut/Secret secret))
    (is (s/validate sut/Secret  {:password-store-multi "path"}))
    (is (s/validate sut/Secret  {:password-store-single "path"}))
    (is (s/validate sut/Secret  {:password-store-record {:path "path"
                                                         :element :login}}))
    (is (s/validate sut/Secret  {:pallet-secret {:service-path [:p]
                                                 :record-element :account
                                                 :key-id "k"}}))
    (is (thrown? Exception (s/validate sut/Secret {:not-implemented ""})))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Tests for replace-secret-schema ;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def schema1
  (s/either
    {:a sut/Secret
     :b s/Any
     :c (s/either s/Int sut/Secret)
     :d [sut/Secret]
     (s/optional-key :e) sut/Secret
     (s/optional-key :f) {:x s/Str
                          :y sut/Secret}}
    [sut/Secret]))

(def schema1-resolved
  (s/either
    {:a s/Str
     :b s/Any
     :c (s/either s/Int s/Str)
     :d [s/Str]
     (s/optional-key :e) s/Str
     (s/optional-key :f) {:x s/Str
                          :y s/Str}}
    [s/Str]))

(def config1
  {:a {:plain "first secret"}
   :b 42
   :c 24
   :d [{:plain "secret"} {:plain "secret2"}]
   :e {:plain "next secret"}
   :f {:x "no secret"
       :y {:plain "secret"}}})

(def config2
  [{:plain "secret"} {:plain "secret2"}])
  
(deftest test-schema-resolving
  (testing
    (is (= s/Str (sut/replace-secret-schema sut/Secret)))
    (is (= {s/Keyword s/Str} (sut/replace-secret-schema {s/Keyword sut/Secret})))
    (is (= [s/Str] (sut/replace-secret-schema [sut/Secret])))
    (is (= schema1-resolved (sut/replace-secret-schema schema1)))))

(deftest test-generic-secret-resolving
  (testing
    (is (s/validate schema1 config1))
    (is (s/validate schema1 config2))
    (is (s/validate schema1-resolved (sut/resolve-secrets schema1 config1)))
    (is (s/validate schema1-resolved (sut/resolve-secrets schema1 config2)))
    (is (thrown? Exception (sut/resolve-secrets schema1 (merge config1 {:a "no secret"}))))
    ))
