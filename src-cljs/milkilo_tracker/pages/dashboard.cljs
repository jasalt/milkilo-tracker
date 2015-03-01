(ns milkilo-tracker.pages.dashboard
  (:require
   [milkilo-tracker.session :as session]
   [reagent.core :as reagent :refer [atom]]
   [secretary.core :as secretary]
   [clojure.string :as str]
   [milkilo-tracker.utils :refer [get-cookie log logout]]
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

   (if-let [site (session/get :site)]
     [:div
      [:h4 "Puhdistamo"]
      [:p (str site)]]
     [:p "Puhdistamo puuttuu (tai sen tietoja ladataan)"])
   
   (if-let [entries (session/get :entries)]
     [:div
      [:h4 "Merkinnät:"]
      (for [entry entries]
        [:p (str entry)])])

   (let [before-at (first (str/split (get-cookie "email") "%40"))
         name (str (str/join " " (map str/capitalize (str/split before-at "."))))]
     [:a.text-center {:on-click #(let [message "Oletko varma että haluat kirjautua ulos?"
                                      dialog-result (.confirm js/window message)]
                                  (when dialog-result (logout)))}
      [:h4 [:small (str "Kirjaudu ulos ("name")")]]])])
