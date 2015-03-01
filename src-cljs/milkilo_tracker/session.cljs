;; Global state, initialized with global data.
(ns milkilo-tracker.session
  (:refer-clojure :exclude [get])
  (:require
   [reagent.core :as reagent :refer [atom]]))

(def state
  (atom
   {:entry_types
    [{:name "Kommentti" :table "comment" :input-type (keyword "text")
      :description "Vapaa kommentti, voidaan käyttää yksinään tai jonkin muun \\
                     merkintätyypin yhteydessä."}
     {:name "Aktiivilietemittaus" :table "silt_active_ml_per_l"
      :input-type (keyword "number") :unit "ml per litra"
      :description "Aktiivilietteen mittaus"}
     {:name "Poistopumppaus" :table "silt_surplus_removal_l"
      :input-type (keyword "number") :unit "litraa"
      :description "Ylijäämälietteen poistomäärä litroina."}
     {:name "Pumpun käyttötunnit" :table "pump_usage_hours"
      :input-type (keyword "number") :unit "tuntia"
      :description "Pumpun käyttötuntilaskurin lukema"}
     {:name "Kirkasvesinäyte" :table "water_quality"
      :input-type (keyword "number") :unit "1-3"
      :description "Kirkasveden laatu asteikolla 1-3 (1 on parhain)"}
     {:name "Ferrosulfaatin määrä" :table "ferrosulphate_level_percent"
      :input-type (keyword "number") :unit "prosenttiluku"
      :description "Ferrosulfaattimittarin prosenttilukema"}
     {:name "Ferrosulfaatin lisäys" :table "ferrosulphate_addition_kg"
      :input-type (keyword "number") :unit "kiloa"
      :description "Ferrosulfaatin määrän lisäys kiloina."}]}))

(defn get [k & [default]]
  (clojure.core/get @state k default))

(defn put! [k v]
  (swap! state assoc k v))

(defn update-in! [ks f & args]
  (clojure.core/swap!
   state
   #(apply (partial update-in % ks f) args)))
