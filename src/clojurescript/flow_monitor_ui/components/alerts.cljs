(ns clojurescript.flow-monitor-ui.components.alerts
  (:require
    [clojurescript.flow-monitor-ui.utils.helpers :refer [<sub]]
    [clojurescript.flow-monitor-ui.subs :as shared-subs]))

(defn alert-display []
  (let [alerts (<sub [::shared-subs/alerts])]
    [:div#alerts
     (for [alert alerts]
       [:div.notification {:key (:uuid alert)
                           :class (str "is-" (:type alert))}
        (:message alert)])]))
