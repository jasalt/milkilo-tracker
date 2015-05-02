(ns milkilo-tracker.pages.dashboard
  (:require
   [milkilo-tracker.session :as session]
   [reagent.core :as reagent :refer [atom]]
   [secretary.core :as secretary]
   [clojure.string :as str]
   [milkilo-tracker.utils :refer [get-cookie log logout
                                  get-entry-info
                                  delete-entry]]))

(defn dashboard-page []
  [:div
   [:br]
   [:a.btn.btn-lg.btn-primary.btn-block
    {:href "#/add-entry"} "Lisää uusi merkintä"]
   [:br]
   (if-let [entries (session/get :entries)]
     ;; TODO show a couple last entries when CLJS subvec reverse bug is fixed.
     (let [last-entries entries
           entry-types (session/get :entry-types)]
       [:div.panel.panel-default
        [:div.panel-heading
         [:h4 "Edelliset merkinnät:"]]
        [:div.panel-body
         "Tarkastele ja poista edellisiä merkintöjä"]
        [:div.list-group
         (doall
          (for [entry last-entries]
            ^{:key entry}
            (let [title (:name (entry-types (entry :type)))
                  date (:date entry)]
              [:div.list-group-item
               [:div.clearfix {:style {:clear "both"}}
                [:span.badge.pull-left {:style {:font-size "1.3em"}} (str title " ")]
                [:button.btn.btn-danger.pull-right
                 {:onClick (delete-entry (assoc entry :site_id ((session/get :site) :id)))} "X"]]
               [:p (str (date :day)"."(date :month)"."(date :year)) " - "[:strong (str (entry :value))]]])))]]))

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
