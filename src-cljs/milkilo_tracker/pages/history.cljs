(ns milkilo-tracker.pages.history
  (:require
   [reagent.core :as reagent :refer [atom]]
   [secretary.core :refer [dispatch!]]

   ))

(defn history []
  [:div
   [:h1 "Historia"]
   [:p "Add cool NVD3 diagrams and stuff"]

   [:ul
    (for [item (@state :data)]
      ^{:key item} [:li [:a {:on-click #(dispatch! (str "#/entry/" (item :id)))}
                         (str "Item: " (item :id) " Date: " (item :date))]])]])
