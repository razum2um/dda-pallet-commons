```clojure
(def ExistingNode
  "Represents a target node with ip and its name."
  {:node-name s/Str
   :node-ip s/Str})

(def ExistingNodes
  "A sequence of ExistingNodes."
  {s/Keyword [ExistingNode]})

(def ProvisioningUser
  "User used for provisioning."
  {:login s/Str
   (s/optional-key :password) secret/Secret})

(def Targets
  "Targets to be used during provisioning."
  {:existing [ExistingNode]
   (s/optional-key :provisioning-user) ProvisioningUser})
```
