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


(ns org.domaindrivenarchitecture.pallet.commons.stevedore-test
  (:require
    [clojure.test :refer :all]
    [clojure.java.io :as io]))

(defn source-comment-re-str []
  (str "(?sm) *# " (.getName (io/file *file*)) ":\\d+\n?"))

;;; a test method that adds a check for source line comment
(defmethod assert-expr 'script= [msg form]
  (let [[_ expected expr] form]
    `(let [re# (re-pattern ~(source-comment-re-str))
           expected# (-> ~expected string/trim)
           actual# (-> ~expr (string/replace re# "") string/trim)]
       (if (= expected# actual#)
         (do-report
          {:type :pass :message ~msg :expected expected# :actual actual#})
         (do-report
          {:type :fail :message ~msg :expected expected# :actual actual#})))))