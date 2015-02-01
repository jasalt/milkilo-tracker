(ns milkilo-tracker.utils
  (:require
   [reagent.core :as reagent :refer [atom]]
   ))

(defn log [x]
  (js/console.log "Log: " (pr-str x))
  (js/console.log  x))
