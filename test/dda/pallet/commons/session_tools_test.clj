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


(ns dda.pallet.commons.session-tools-test
  (:require
    [clojure.test :refer :all]
    [schema.core :as s]
    [dda.pallet.commons.session-tools :as sut]))



(def schema-plain {:a s/Any :b s/Any})
(def value-plain-1 {:a 1 :b 2 :c 3})
(def value-plain-2 {:a 1 :c 3})

(def schema-vector-1 {:bs s/Any})
(def schema-vector-2 {:bs [{:b1 s/Any}]})
(def value-vector {:a 1
                   :bs [{:b1 21}
                        {:b1 22}
                        {:b1 23}
                        nil
                        {:b1 241
                         :b2 242}]})

(def schema-list-1 {:bs s/Any})
(def schema-list-2 {:bs [{:b1 s/Any}]})
(def value-list {:a 1
                 :bs '({:b1 21}
                      {:b1 22}
                      nil
                      {:b1 23}
                      {:b1 241
                       :b2 242})})

(def schema-deep {:bs [{:b1 s/Any 
                        :b2 {:b2-1 s/Any
                             :b2-2 s/Any}}]})
(def value-deep {:a 1
                 :bs [{:b1 21
                       :b2 {:b2-1 211
                            :b2-2 212
                            :b2-3 213}}
                      {:b1 22}
                      {:b1 23}
                      {:b1 24}]})

(deftest test-filter
  (testing 
    "filter plain schema" 
    (is 
      (= {:a 1, :b 2}
         (sut/filter-for-schema schema-plain value-plain-1)))
    (is 
      (= {:a 1 }
         (sut/filter-for-schema schema-plain value-plain-2))))
  (testing 
    "nested vector" 
    (is 
      (= {:bs [{:b1 21} {:b1 22} {:b1 23} nil {:b1 241, :b2 242}]}
         (sut/filter-for-schema schema-vector-1 value-vector)))
    (is
      (= {:bs [{:b1 21} {:b1 22} {:b1 23} {:b1 241}]}
         (sut/filter-for-schema schema-vector-2 value-vector))))
  (testing 
    "nested list" 
    (is 
      (= {:bs '({:b1 21} {:b1 22} nil {:b1 23} {:b1 241 :b2 242})}
         (sut/filter-for-schema schema-list-1 value-list)))
    (is
      (= {:bs '({:b1 21} {:b1 22} {:b1 23} {:b1 241})}
           (sut/filter-for-schema schema-list-2 value-list))))
  (testing 
    "nested map in vector" 
    (is 
      (= {:bs
          [{:b1 21, :b2 {:b2-1 211, :b2-2 212}}
           {:b1 22}
           {:b1 23}
           {:b1 24}]}
         (sut/filter-for-schema schema-deep value-deep))))
  )
