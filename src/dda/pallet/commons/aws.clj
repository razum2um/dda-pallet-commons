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

(ns dda.pallet.commons.aws
  (:require
    [schema.core :as s]
    [pallet.api :as api]
    [pallet.compute :as compute]
    [dda.pallet.commons.secret :as secret]))

; TODO: refactor - move to config commons
(def AwsContext
 {:account secret/PalletSecret
  :secret secret/PalletSecret
  :region s/Str
  :subnet-ids [s/Str]})

(def AwsContextResolved
 {:account s/Str
  :secret s/Str
  :region s/Str
  :subnet-ids [s/Str]})

; TODO: refactor - move to config commons
(def ProvisioningUser {:login s/Str
                       :ssh-key-name s/Str})

; TODO: refactor - move to config commons
(def AwsNodeSpec
  {:region s/Str
   :ami-id s/Str
   :hardware-id s/Str
   :security-group-ids [s/Str]
   :subnet-id s/Str
   :provisioning-user ProvisioningUser})

; TODO: refactor - move to config commons
(def Targets {:context AwsContext
              :node-spec AwsNodeSpec})

(defn meissa-unencrypted-context
  []
  (let
    [aws-decrypted-credentials (get-in (pallet.configure/pallet-config) [:services :aws])]
    {:account {:plain (get-in aws-decrypted-credentials [:account])}
     :secret {:plain (get-in aws-decrypted-credentials [:secret])}
     :region "eu-central-1"
     :subnet-ids ["subnet-f929df91"]}))

(defn meissa-encrypted-context
  [key-id]
  {:account {:pallet-secret {:service-path [:services :aws]
                             :record-element :account
                             :key-id key-id}}
   :secret {:pallet-secret {:service-path [:services :aws]
                            :record-element :secret
                            :key-id key-id}}
   :region "eu-central-1"
   :subnet-ids ["subnet-f929df91"]})

(defn meissa-default-node-spec
  [ssh-key-name]
  {:region "eu-central-1a"
   :ami-id "ami-82cf0aed"
   :hardware-id "t2.micro"
   :security-group-ids ["sg-0606b16e"]
   :subnet-id "subnet-f929df91"
   :provisioning-user {:login "ubuntu"
                       :ssh-key-name ssh-key-name}})

(defn- realize-provider
  [resolved-aws-context]
  (let [{:keys [account secret region subnet-ids]} resolved-aws-context]
    (compute/instantiate-provider
      :pallet-ec2
      :identity account
      :credential secret
      :endpoint region
      :subnet-ids subnet-ids)))

(s/defn dispatch-by-argument-count :- s/Keyword
  [& args]
  (cond
    (= 0) :unencrypted-context
    (= 1) :external-encrypted-context
    (= 3) :internal-encrypted-context))

(defmulti provider dispatch-by-argument-count)
(s/defmethod provider :unencrypted-context
  []
  (realize-provider (secret/resolve-secret (meissa-unencrypted-context))))
(s/defmethod provider :external-encrypted-context
  [context :- AwsContext]
  (realize-provider (secret/resolve-secret context)))
(s/defmethod provider :internal-encrypted-context
  [key-id :- s/Str
   passphrase :- s/Str
   context :- AwsContext]
  (realize-provider (secret/resolve-secret
                     (meissa-encrypted-context key-id)
                     passphrase)))

(s/defn node-spec
  [aws-node-spec :- AwsNodeSpec]
  (let [{:keys [region ami-id hardware-id security-group-ids
                subnet-id provisioning-user]} aws-node-spec
        {:keys [login ssh-key-name]} provisioning-user]
    (api/node-spec
      :location {:location-id region}
      :image {:os-family :ubuntu
              ;eu-central-1 16-04 LTS hvm
              :image-id ami-id
              ;eu-west1 16-04 LTS hvm :image-id "ami-07174474"
              ;us-east-1 16-04 LTS hvm :image-id "ami-45b69e52"
              :os-version "16.04"
              :key-name ssh-key-name
              :login-user login}
      :hardware {:hardware-id hardware-id}
      :provider {:pallet-ec2 {:network-interfaces [{:device-index 0
                                                    :groups security-group-ids
                                                    :subnet-id subnet-id
                                                    :associate-public-ip-address true
                                                    :delete-on-termination true}]}})))
