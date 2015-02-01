;; Global state
(ns milkilo-tracker.session
  (:refer-clojure :exclude [get])
  (:require
   [reagent.core :as reagent :refer [atom]]))

;; (defonce state (atom {:saved? false
;;                       :data []
;;                       :entry-id nil
;;                       :bread nil
;;                       }))

(def state (atom {}))

(defn get [k & [default]]
  (clojure.core/get @state k default))

(defn put! [k v]
  (swap! state assoc k v))

(defn update-in! [ks f & args]
  (clojure.core/swap!
   state
   #(apply (partial update-in % ks f) args)))
