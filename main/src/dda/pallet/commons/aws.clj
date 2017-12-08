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
    [dda.pallet.commons.external-config :as ext-config]
    [dda.config.commons.secret :as secret]))

; TODO: refactor - move to config commons
(def AwsContext
 {:key-id secret/PalletSecret
  :key-secret secret/PalletSecret
  :region s/Str
  :subnet-ids [s/Str]})

(def AwsContextResolved
 {:key-id s/Str
  :key-secret s/Str
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

; TODO: refactor - move to config commons
(s/defn ^:always-validate load-targets :- Targets
  [file-name :- s/Str]
  (ext-config/parse-config file-name))

(s/defn ^:always-validate meissa-unencrypted-context :- AwsContext
  []
  (let
    [aws-decrypted-credentials (get-in (pallet.configure/pallet-config) [:services :aws])]
    {:key-id {:plain (get-in aws-decrypted-credentials [:account])}
     :key-secret {:plain (get-in aws-decrypted-credentials [:secret])}
     :region "eu-central-1"
     :subnet-ids ["subnet-f929df91"]}))

(s/defn ^:always-validate meissa-encrypted-context :- AwsContext
  [key-id :- s/Str]
  {:key-id {:pallet-secret {:service-path [:services :aws]
                            :record-element :account
                            :key-id key-id}}
   :key-secret {:pallet-secret {:service-path [:services :aws]
                                :record-element :secret
                                :key-id key-id}}
   :region "eu-central-1"
   :subnet-ids ["subnet-f929df91"]})

(s/defn meissa-default-node-spec :- AwsNodeSpec
  [ssh-key-name :- s/Str]
  {:region "eu-central-1a"
   :ami-id "ami-82cf0aed"
   :hardware-id "t2.micro"
   :security-group-ids ["sg-0606b16e"]
   :subnet-id "subnet-f929df91"
   :provisioning-user {:login "ubuntu"
                       :ssh-key-name ssh-key-name}})

(s/defn resolve-secrets :- AwsContextResolved
  ([context :- AwsContext]
   (let [{:keys [key-id key-secret]} context]
     (merge
       context
       {:key-id (secret/resolve-secret key-id)
        :key-secret (secret/resolve-secret key-secret)})))
  ([context :- AwsContext
    passphrase :- s/Str]
   (let [{:keys [key-id key-secret]} context]
     (println key-id)
     (merge
       context
       {:key-id (secret/resolve-secret key-id :passphrase passphrase)
        :key-secret (secret/resolve-secret key-secret :passphrase passphrase)}))))

(defn- realize-provider
  [resolved-aws-context]
  (let [{:keys [key-id key-secret region subnet-ids]} resolved-aws-context]
    (compute/instantiate-provider
      :pallet-ec2
      :identity key-id
      :credential key-secret
      :endpoint region
      :subnet-ids subnet-ids)))

(s/defn ^:always-validate dispatch-by-argument-count :- s/Keyword
  [& args]
  (cond
    (= (count args) 0) :unencrypted-context
    (= (count args) 1) :external-encrypted-context
    (= (count args) 3) :internal-encrypted-context))

(defmulti provider dispatch-by-argument-count)
(s/defmethod provider :unencrypted-context
  [& _]
  (realize-provider (resolve-secrets (meissa-unencrypted-context))))
(s/defmethod ^:always-validate provider :external-encrypted-context
  [context :- AwsContext
   & _]
  (realize-provider (resolve-secrets context)))
(s/defmethod ^:always-validate provider :internal-encrypted-context
  [key-id :- s/Str
   passphrase :- s/Str
   context :- AwsContext
   & _]
  (realize-provider (resolve-secrets
                     (meissa-encrypted-context key-id)
                     passphrase)))

(s/defn ^:always-validate node-spec
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
