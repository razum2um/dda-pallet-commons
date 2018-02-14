(ns dda.pallet.commons.session-target
  (:require
    [schema.core :as s]))

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
