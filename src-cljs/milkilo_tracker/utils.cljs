(ns milkilo-tracker.utils
  (:require
   [reagent.core :as reagent :refer [atom]]
   [ajax.core :refer [GET]]
   [goog.net.cookies]
   ))

(defn get-cookie [key]
  (.get goog.net.cookies (name key))
  )

(defn log [x]
  (js/console.log "Log: " (pr-str x))
  (js/console.log  x))

(defn logout []
  (GET "/logout") ;; Response drops session cookie
  (.replace js/location "/login") ;; TODO hacky way to redirect client after logout
  )
