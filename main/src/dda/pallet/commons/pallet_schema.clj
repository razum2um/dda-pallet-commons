(ns dda.pallet.commons.pallet-schema
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

(def TargetSpec
  {:phases s/Any
   :default-phases s/Any
   :hardware s/Any
   :count s/Num
   :image s/Any
   :location s/Any
   :provider s/Any
   :group-name s/Keyword
   :node s/Any})
