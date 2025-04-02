(ns clojurescript.flow-monitor-ui.subs
  (:require
    [re-frame.core :as rf]))


; = Register Subscriptions ===================================================== {{{
;; https://github.com/Day8/re-frame/blob/master/docs/Loading-Initial-Data.md#the-pattern
(rf/reg-sub
  ::initialized?
  (fn [db _]
    (and (not (empty? db))
         true)))

(rf/reg-sub
  ::active-route
  (fn [db _]
    (:active-route db)))

(rf/reg-sub
  ::alerts
  (fn [db _]
    (:alerts db)))
;; }}}