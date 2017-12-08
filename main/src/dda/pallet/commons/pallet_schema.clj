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

(def SessionSpec
  {
  :new-nodes s/Any
  :old-nodes s/Any
  :targets s/Any
  :plan-state s/Any
  :service-state s/Any
  :results [{:target {:phases s/Any
                      :default-phases s/Any
                      :hardware s/Any
                      :count s/Num
                      :image s/Any
                      :location s/Any
                      :provider s/Any
                      :group-name s/Keyword
                      :node s/Any}
             :target-type s/Any
             :plan-state s/Any
             :result '({:context s/Any
                        :action-symbol s/Any
                        :out s/Any
                        :exit s/Any
                        :flags s/Any 
                        :flag-values s/Any
                        :script s/Any
                        :summary s/Any})
             :phase s/Any
             }]
  :environment s/Any
  :initial-plan-state s/Any})