(ns clojurescript.flow-monitor-ui.utils.helpers
  (:require [re-frame.core :as rf]))

(defn remove-matching-uuid [elements uuid]
  (remove #(= uuid (:uuid %)) elements))

(def <sub (comp deref rf/subscribe))
(def >dis rf/dispatch)
