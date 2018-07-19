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
  "{
0
}")

(def no-error-2
  "{
0\n
}")

(def error
  "{
3
}")

(deftest test-parse-result-boundaries
  (testing
    "test parsing ls output"
      (is (= "0"
             (sut/parse-result-boundaries no-error-1)))
     (is (= "0\n"
            (sut/parse-result-boundaries no-error-2)))
     (is (= "3"
            (sut/parse-result-boundaries error)))))

(deftest test-parse
  (testing
    "test parsing ls output"
      (is (sut/parse-exit-code (sut/parse-result-boundaries no-error-1)))
      (is (sut/parse-exit-code (sut/parse-result-boundaries no-error-2)))
      (is (not (sut/parse-exit-code (sut/parse-result-boundaries error))))))
