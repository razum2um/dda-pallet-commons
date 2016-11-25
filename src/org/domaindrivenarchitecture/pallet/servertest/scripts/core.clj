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

(ns org.domaindrivenarchitecture.pallet.servertest.scripts.core
  (:require
    [pallet.crate :as crate]
    [pallet.actions :as actions]
    [pallet.script :as script]
    [pallet.script.lib :as lib]
    [pallet.stevedore :refer :all]))
    

(defn- create-resource-timestamp
  "Creates a timestamp to create state directories."
  []
  (.format (java.text.SimpleDateFormat. "yyyyMMdd-HHmmss.SSS") (new java.util.Date)))

(def resource-folder-path-base
  "/home/pallet/state/resources-")

(defn- resource-folder-path
   "Creates the resource folder and sets its path to the settings if no
   path is set yet."
  []
  (let [settings-resource-path (-> (crate/get-settings :dda-pallet-commons) :resource-path)]
    (if settings-resource-path
      settings-resource-path
      (do
        (let [create-resource-path (str resource-folder-path-base (create-resource-timestamp))]
        (crate/assoc-settings :dda-pallet-commons {:resource-path create-resource-path})
        (actions/directory 
          create-resource-path
          :owner "pallet" :group "pallet" :mode "700")
        (actions/file
          (str resource-folder-path-base "current")
          :action :delete)  
        (actions/symbolic-link
          create-resource-path
          (str resource-folder-path-base "current")
          :owner "pallet" :group "pallet" :mode "700")         
        create-resource-path))
      )))

(defn keyword-to-filename
  "Transforms a (possibly) namespaced keyword used as resource-key
   to a filename for the resource."
  [resource-key]
  (str (or (namespace resource-key) "no-namespace") "." (name resource-key)))

(defn resource-file-path
  "Path to the resource file of a given resource-key."
  [resource-key]
  (str (resource-folder-path) "/" (keyword-to-filename resource-key) ".rc"))

(defn resource-script-path
  "Path to the script for creation of the resource file of a given resource-key."
  [resource-key]
  (str (resource-folder-path) "/" (keyword-to-filename resource-key) ".sh"))

(script/defscript script-run-resource 
  "This script (explained in stevedore) runs the resource script and copys
   the output to stdout (for the result in the session) and to the resource
   file on the remote machine.

   It fails if the script fails or the resource file cannot be created."
  [resource-key])
(script/defimpl script-run-resource :default [resource-key]
  ("set -o pipefail")
  (if
    (pipe 
      (resource-script-path ~resource-key) 
      ("tee" (resource-file-path ~resource-key)))
    (lib/exit 0)
    (lib/exit 1)))

(script/defscript script-test-resource
  "This script creates the environment for the test script, the noticable
   things are:
    * the testscript will receive the resource piped on stdin
    * the exitcode of the script is checked and transformed to an output
      (to allow the test to continue)"
  [resource-key test-script])
(script/defimpl script-test-resource :default [resource-key test-script]
  (defn testscript [] ~test-script)
  (pipe
    ("cat" (resource-file-path ~resource-key))
    ("testscript"))
  ; print out exit code of testscript and always exitwith code 0
  (println "$?")
  (lib/exit 0))

(script/defscript script-test-not-empty
  "Tests if input from stdin has positive byte count. If used with :strip true
   option whitespaces, \n and \r are ignored in the count."
  [& {:keys [strip] :or {strip false}}])
(script/defimpl script-test-not-empty :default [& {:keys [strip] :or {strip false}}]
  (if (= 0 @(~(if strip 
                "tr -d \" \\r\\n\" | wc -c | cut -d' ' -f1" 
                "wc -c | cut -d' ' -f1")))
    (do 
      (println "FAIL:" ~(if strip "stripped" "raw") "file is empty")
      (lib/exit 1))
    (do 
      (println "PASS:" ~(if strip "stripped" "raw") "file has content")
      (lib/exit 0))))

(script/defscript script-test-match-regexp
  "Prints all matching lines and has successful exit code if at least one line
   matches."
  [regexp])
(script/defimpl script-test-match-regexp :default [regexp]
  (println "All matches for regexp ,," ~regexp "`` using grep:")
  (if ("grep" ~regexp)
    (lib/exit 0)
    (do
      (println "No matches found, test FAILED.")
      (lib/exit 1))))