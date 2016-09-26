(ns org.domaindrivenarchitecture.pallet.commons.pallet-schema
  (:require 
    [schema.core :as s]))

(def PhasePlanSpec
  {:phases s/Any
   :default-phases s/Any})

(def ServerSpec
  {:phase s/Any
   :session s/Any
   :default-phases s/Any
   :phases-meta    s/Any
   :extends        s/Any
   :roles          s/Any
   :node-spec      s/Any
   :packager       s/Any})

(def SessionSpec
  s/Any)