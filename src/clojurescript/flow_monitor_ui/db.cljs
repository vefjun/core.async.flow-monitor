(ns clojurescript.flow-monitor-ui.db
  (:require
    [clojure.spec.alpha :as s]
    [clojurescript.flow-monitor-ui.routes.index.db :as index]))


;; = Specs ===================================================================== {{{
(s/def ::active-route keyword?)
(s/def ::map-of-keywords-and-strs (s/every-kv keyword string?))
(s/def ::uuid uuid?)
;; - Active Route -------------------------------------------------------------- {{{2
(s/def ::name keyword?)
(s/def ::view vector?)
(s/def ::dispatch-on-entry vector?)
(s/def ::path-params ::map-of-keywords-and-strs)
(s/def ::query-params ::map-of-keywords-and-strs)
(s/def ::data (s/keys :req-un [::name ::view]
                      :opt-un [::dispatch-on-entry]))
(s/def ::active-route (s/keys :req-un [::data ::path-params ::query-params]))
;; }}}2

;; -- Route DBs ---------------------------------------------------------------- {{{2
(s/def ::index ::index/index-db)
(s/def ::routes (s/keys :req-un [::index]))
;; }}}2

;; -- Alerts ------------------------------------------------------------------- {{{2
(s/def ::message string?)
(s/def ::type string?)
(s/def ::alert (s/keys :req-un [::uuid ::message ::type]))
(s/def ::alerts (s/* ::alert))
;; }}}2
;; }}}

;; = DB Spec =================================================================== {{{
(s/def ::db (s/keys :req-un [::active-route
                             ::alerts
                             ::routes]))

(defonce default-db {;; = Top Level ============================================
                     :active-route {:path-params {}
                                    :query-params {}
                                    :data {:name :loading
                                           :view [:div "Loading..."]}}
                     :alerts '()
                     ;; = index Route ==========================================
                     :routes {:index index/index-db}})
;; }}}

