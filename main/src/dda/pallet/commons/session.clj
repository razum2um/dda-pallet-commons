(ns dda.pallet.commons.session
  (:require
    [schema.core :as s]
    [dda.pallet.commons.session-result :as sr]))

(def SessionSpec
  {:new-nodes s/Any
   :old-nodes s/Any
   :targets s/Any
   :plan-state s/Any
   :service-state s/Any
   :results [{:target TargetSpec
              :target-type s/Any
              :plan-state s/Any
              :result '(sr/ResultSpec)
              :phase s/Any}]
   :environment s/Any
   :initial-plan-state s/Any})
