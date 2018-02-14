(ns dda.pallet.commons.session-result
  (:require
    [schema.core :as s]))

(def ResultSpec
  {:context s/Any
   :action-symbol s/Any
   :out s/Any
   :exit s/Any
   :flags s/Any
   :flag-values s/Any
   :script s/Any
   :summary s/Any})
