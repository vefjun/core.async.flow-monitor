(ns clojurescript.flow-monitor-ui.interceptors
  (:require
    [re-frame.core :as rf]
    [re-frame.core :as rf]))

(def debugger
  (rf/->interceptor
    :id    :debugger
    :after (fn [{:keys [effects] {:keys [event db]} :coeffects :as context}]
             context)))

;; = Additional Interceptors Can Be Added To The Vector As Needed
(def standard-interceptors [debugger
                            ;; Turn on for shitty debugging situations
                            ; (when ^boolean goog.DEBUG rf/debug)
                            ;; Spec check the db this can be agressive, but useful
                            ; (when ^boolean goog.DEBUG (rf/after check-and-throw))
                            ])
