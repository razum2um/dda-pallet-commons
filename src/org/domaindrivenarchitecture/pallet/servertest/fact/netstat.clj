(ns org.domaindrivenarchitecture.pallet.servertest.fact.netstat
  (:require
    [org.domaindrivenarchitecture.pallet.servertest.core.fact :refer :all]))

(def fact-id-netstat ::netstat)

(defn parse-netstat
  [netstat-resource]
  (map #(zipmap 
          [:proto :recv-q :send-q :local-adress :foreign-adress :state :user :inode :pid :process-name]
          (clojure.string/split (clojure.string/trim %) #"\s+|/"))
     (drop-while #(not (re-matches #"\s*(tcp|udp).*" %)) 
       (clojure.string/split netstat-resource #"\n"))))

(defn collect-netstat-fact
  "Defines the netstat resource. 
   This is automatically done serverstate crate is used."
  []
  (collect-fact fact-id-netstat '("netstat" "-tulpen") :transform-fn parse-netstat))