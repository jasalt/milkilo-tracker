(ns milkilo-tracker.pages.edit-entry
  (:require
   [reagent.core :as reagent :refer [atom]]
   [secretary.core :refer [dispatch!]]

   ))

(defn edit-entry [entry-id]
  [:div
   [:h1 (str "Muokkaa merkintää " (@state :entry-id))]
   [:button
    {:class "btn btn-lg btn-success"
     :on-click #(dispatch! "#/")} "Tallenna"]
   [:button
    {:class "btn btn-lg btn-warning"
     :on-click #(dispatch! "#/")} "Peruuta"]
   [:button
    {:class "btn btn-lg btn-danger"
     :on-click #(js/alert "TODO: remove")} "Poista"]])
