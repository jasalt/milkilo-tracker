;; Global state, initialized with global data.
(ns milkilo-tracker.session
  (:refer-clojure :exclude [get])
  (:require
   [reagent.core :as reagent :refer [atom]]))

(def entry-types-map
  {
   :comment 
   {:name "Kommentti" :input-type (keyword "textarea")
    :description "Vapaa kommentti, voidaan käyttää yksinään tai jonkin muun
                     merkintätyypin yhteydessä."}
   :silt_active_ml_per_l 
   {:name "Aktiivilietemittaus" 
    :input-type (keyword "numeric") :unit "ml per litra"
    :description "Aktiivilietteen mittaus"}
   :silt_surplus_removal_l 
   {:name "Poistopumppaus" 
    :input-type (keyword "numeric") :unit "litraa"
    :description "Ylijäämälietteen poistomäärä litroina."}
   :pump_usage_hours 
   {:name "Pumpun käyttötunnit" 
    :input-type (keyword "numeric") :unit "tuntia"
    :description "Pumpun käyttötuntilaskurin lukema"}
   :water_quality 
   {:name "Kirkasvesinäyte" 
    :input-type (keyword "numeric") :unit "1-3"
    :description "Kirkasveden laatu asteikolla 1-3 (1 on parhain)"}
   :ferrosulphate_level_percent 
   {:name "Ferrosulfaatin määrä" 
    :input-type (keyword "numeric") :unit "prosenttiluku"
    :description "Ferrosulfaattimittarin prosenttilukema"}
   :ferrosulphate_addition_kg 
   {:name "Ferrosulfaatin lisäys" 
    :input-type (keyword "numeric") :unit "kiloa"
    :description "Ferrosulfaatin määrän lisäys kiloina."}})

(def state
  (atom {:entry-types entry-types-map}))

(defn get [k & [default]]
  (clojure.core/get @state k default))

(defn put! [k v]
  (swap! state assoc k v))

(defn update-in! [ks f & args]
  (clojure.core/swap!
   state
   #(apply (partial update-in % ks f) args)))
