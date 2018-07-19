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

(ns dda.pallet.commons.fact-test
  (:require
    [clojure.test :refer :all]
    [schema.core :as s]
    [dda.pallet.commons.fact :as sut]))

(def no-error-1
  "FACT_START
0
FACT_END")

(def no-error-2
  "FACT_START
0\n
FACT_END")

(def error
  "FACT_START
3
FACT_END")

(def free-command
  "[sudo] Passwort für initial: collect fact for facility: :dda-serverspec-fact fact-key: :dda.pallet.dda-serverspec-crate.infra.fact.command/command (fact.clj:105)...
FACT_START
find--absent
find: ‘/absent’: Datei oder Verzeichnis nicht gefunden
1
----- command output separator -----
FACT_END")

(deftest test-parse-result-boundaries
  (testing
    "test parsing ls output"
    (is (= "0"
           (sut/parse-result-boundaries no-error-1)))
    (is (= "0\n"
           (sut/parse-result-boundaries no-error-2)))
    (is (= "3"
           (sut/parse-result-boundaries error)))
    (is (= "find--absent
find: ‘/absent’: Datei oder Verzeichnis nicht gefunden
1
----- command output separator -----"
           (sut/parse-result-boundaries free-command)))))

(deftest test-parse
  (testing
    "test parsing ls output"
      (is (sut/parse-exit-code (sut/parse-result-boundaries no-error-1)))
      (is (sut/parse-exit-code (sut/parse-result-boundaries no-error-2)))
      (is (not (sut/parse-exit-code (sut/parse-result-boundaries error))))))
