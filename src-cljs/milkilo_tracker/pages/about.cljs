(ns milkilo-tracker.pages.about
  (:require
   [reagent.core :as reagent :refer [atom]]
   ))

(defn about []
  [:div
   [:h1 "Tietoja sovelluksesta"]
   [:p "todo"]])
