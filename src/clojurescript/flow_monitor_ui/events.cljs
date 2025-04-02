(ns clojurescript.flow-monitor-ui.events
  (:require
    [clojure.string :as string]
    [re-frame.core :as rf]
    [lambdaisland.glogi :as log]
    [clojurescript.flow-monitor-ui.config :as config]
    [clojurescript.flow-monitor-ui.interceptors :refer [standard-interceptors]]
    [clojurescript.flow-monitor-ui.utils.helpers :as helpers]
    [clojurescript.flow-monitor-ui.db :as db]))


; region = Register CoFx ==============================================================

; Usage: When registering a event (rf/inject-cofx ::uuid) as an interceptor
; Description: A cofx to inject a uuid into the event.
(rf/reg-cofx
  ::uuid
  (fn [coeffects _]
    (assoc coeffects :uuid (random-uuid))))
;; endregion

; region = Register Effect Handlers ===================================================

; Usage: In the returned map of a reg-event-fx {:save-in-local-storage {:key1 value1 :key2 value2}}
; Description: Saves values form a map into localStorage
(rf/reg-fx
  ::save-in-local-storage
  (fn [map-to-store]
    (doseq [[k v] map-to-store]
      (.setItem js/localStorage (name k) v))))

; Usage: In the returned map of a reg-event-fx {::remove-from-local-storage [:user :username]}
; Description: Removes desired values from localStorage
(rf/reg-fx
  ::remove-from-local-storage
  (fn [keys]
    (doseq [k keys]
      (.removeItem js/localStorage (name k)))))
; endregion

; region = Register Events ============================================================

; region -- App Start Up --------------------------------------------------------------
(defn get-from-ls
  "Attempt to retrieve ls-key from localStorage. If value is equal to 'null'
   return nil"
  [ls-key]
  (let [value (.getItem js/localStorage ls-key)]
    (if (= "null" value) nil value)))

(rf/reg-event-fx
  ::initialize-db
  [standard-interceptors]
  (fn [{:keys []} _]
    {:db db/default-db}))
;; endregion

; region -- Routing -------------------------------------------------------------------
(defn update-events-with-actual-params
  "Events defined in the router to be dispatched when a route is navigated to
   use `:query-params` and `:path-params` as place holders for the actual values
   defined later."
  [events path query]
  (reduce (fn [res event]
            (conj res (mapv (fn [el]
                              (case el
                                :query-params query
                                :path-params path
                                el))
                            event)))
          []
          events))

(rf/reg-event-fx
  ::dispatch-route-events
  [standard-interceptors]
  (fn [_ [_ {:keys [path-params query-params] :as active-route}]]
    (let [route-initialization-events (get-in active-route [:data :dispatch-on-entry] [])]
      {:dispatch-n (update-events-with-actual-params route-initialization-events path-params query-params)})))

; Usage: (dispatch [::shared-events/set-active-route (rfront/match-by-path router/routes uri)])
; Description: Change the active reitit route for the app
(rf/reg-event-fx
  ::set-active-route
  [standard-interceptors]
  (fn [{:keys [db]} [_ {:keys [path-params query-params] :as route}]]
    {:db (-> db
             (assoc :active-route route)
             (assoc :path-params path-params)
             (assoc :query-params query-params))}))
;; endregion

; region -- Alerts --------------------------------------------------------------------

; Usage: ::add-alert and ::remove-alert are typically used at the same time.
;        In a reg-event-fx like so:
;        {:dispatch       [::add-alert (:uuid cofx) "Alert body here" "Alert Type ie: danger, info, etc"]
;         :dispatch-later [{:ms config/alert-timeout-ms
;                           :dispatch [::remove-alert (:uuid cofx)]}]}
; Description: Extends the top level `:alerts` vector with a alert map {:uuid #uuid :type "..." :message "..."}
(rf/reg-event-db
  ::add-alert
  [standard-interceptors]
  (fn [db [_ uuid message alert-type]]
    (update db :alerts merge {:uuid uuid :message message :type alert-type})))

; Description: Removes the alert with the matching uuid from the top-level
;              `:alerts` vector. See above for additional detail
(rf/reg-event-db
  ::remove-alert
  [standard-interceptors]
  (fn [db [_ uuid]]
    (update db :alerts helpers/remove-matching-uuid uuid)))

(rf/reg-event-fx
  ::inject-complete-alert
  [standard-interceptors
   (rf/inject-cofx ::uuid)]
  (fn [{:keys [uuid]} [_ message]]
    {:dispatch [::add-alert uuid message "success"]
     :dispatch-later [{:ms config/alert-timeout-ms
                       :dispatch [::remove-alert uuid]}]}))


(rf/reg-event-fx
  ::websocket-server-closed-alert
  [standard-interceptors
   (rf/inject-cofx ::uuid)]
  (fn [{:keys [uuid]} [_ message]]
    {:dispatch [::add-alert uuid message "error"]
     :dispatch-later [{:ms 5000
                       :dispatch [::remove-alert uuid]}]}))
;; endregion

;; endregion

;; endregion

