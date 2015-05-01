(ns milkilo-tracker.pages.dashboard
  (:require
   [milkilo-tracker.session :as session]
   [reagent.core :as reagent :refer [atom]]
   [secretary.core :as secretary]
   [clojure.string :as str]
   [milkilo-tracker.utils :refer [get-cookie log logout
                                  get-entry-info]]
   ))

(defn dashboard-page []
  [:div
   [:br]
   [:button.btn.btn-lg.btn-primary.btn-block
    {:on-click #(secretary/dispatch! "#/add-entry")} "Lisää uusi merkintä"]

   [:br]

   (if-let [entries (session/get :entries)]
     ;; TODO show a couple last entries when CLJS subvec reverse bug is fixed.
     (let [last-entries entries
           entry-types (session/get :entry-types)]
       [:div.panel.panel-default
        [:div.panel-heading
         [:h4 "Edelliset merkinnät:"]]
        [:div.panel-body 
         "Testaustarkoituksiin näytetään nämä kaikki"]
        [:div.list-group
         (for [entry last-entries]
           ^{:key entry}
           (let [title (:name (entry-types (entry :type)))
                 date (:date entry)]
             [:a.list-group-item.clearfix
              {:on-click #(secretary/dispatch! (str "#/edit-entry/" (entry :id)))}
              [:h3.pull-right (str (entry :value))]
              [:span.badge.pull-left {:style {:font-size "2em"}} (str title)]
              [:br]
              [:h3 (str (date :day)"."(date :month)"."(date :year))]]))]]))

   (if-let [site (session/get :site)]
     [:div
      [:h4 "Puhdistamo (testiarvo)"]
      [:p (str site)]]
     [:p "Puhdistamo puuttuu (tai sen tietoja ladataan)"])

   (let [before-at (first (str/split (get-cookie "email") "%40"))
         name (str (str/join " " (map str/capitalize (str/split before-at "."))))]
     [:a.text-center {:on-click #(let [message "Oletko varma että haluat kirjautua ulos?"
                                       dialog-result (.confirm js/window message)]
                                   (when dialog-result (logout)))}
      [:h4 [:small (str "Kirjaudu ulos ("name")")]]])])
