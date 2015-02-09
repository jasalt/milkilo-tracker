(ns milkilo-tracker.utils
  (:require
   [reagent.core :as reagent :refer [atom]]
   [goog.net.cookies]
   ))

(defn get-cookie [key]
  (.get goog.net.cookies (name key))
  )

(defn log [x]
  (js/console.log "Log: " (pr-str x))
  (js/console.log  x))
