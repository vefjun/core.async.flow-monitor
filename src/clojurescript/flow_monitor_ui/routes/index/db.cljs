(ns clojurescript.flow-monitor-ui.routes.index.db
  (:require
    [clojure.spec.alpha :as s]))

;; = Functions =================================================================

;; = Specs =====================================================================
(s/def ::index-db (s/keys :req-un []))

;; = Default Index Route DB Map ================================================
(defonce index-db {:editor-contents {}})
