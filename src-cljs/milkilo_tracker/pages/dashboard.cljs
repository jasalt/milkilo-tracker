(ns milkilo-tracker.pages.dashboard
  (:require
   [milkilo-tracker.session :as session]
   [reagent.core :as reagent :refer [atom]]
   [secretary.core :as secretary]
   ))

(defn dashboard-page []
  [:div
   [:button.btn.btn-lg.btn-primary.btn-block
    {:on-click #(secretary/dispatch! "#/add-entry")} "Lisää uusi merkintä"]
   [:div.chart-container
    [:p "Diagram:"]
    [:img.img-responsive
     {:src "http://placekitten.com.s3.amazonaws.com/homepage-samples/200/138.jpg"
      :on-click #(secretary/dispatch! "#/history")}]]

   (if-let [last-entry (first (session/get :data))]
     [:div
      [:p "Viimeisin mittaus"]
      [:a {:on-click #(secretary/dispatch! (str "#/entry/" (last-entry :id)))}
       (str "Item: " (last-entry :id) " Date: " (last-entry :date)) ]]

     [:p "Ei mittauksia.."])])
