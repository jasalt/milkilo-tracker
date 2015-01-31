(ns milkilo-tracker.core
  (:require [reagent.core :as reagent :refer [atom]]
            [secretary.core :as secretary]
            [reagent-forms.core :refer [bind-fields]]
            [ajax.core :refer [POST, GET]]
            )
  (:require-macros [secretary.core :refer [defroute]])
  )

(defn log [x]
  (js/console.log "Log: " (pr-str x))
  (js/console.log  x)
  )

(def state (atom {:saved? false}
                 {:data []}
                 {:entry-id nil}
                 {:bread nil}))

(defn row [label & body]
  [:div.row
   [:div.col-md-2 [:span label]]
   [:div.col-md-3 body]])

(defn breadcrumbs []
  (js/console.log (@state :bread))
  [:ol.breadcrumb
   
   [:li {:class (when (= home (:page @state)) "active")}
    [:a {:on-click #(secretary/dispatch! "#/")} "Dashboard"]]

   (if (@state :bread)
     [:li.active (@state :bread)]
     )
   ]
  )

(defn navbar []
  [:div.navbar.navbar-default
   [:div.navbar-collapse.collapse
    [:ul.nav.navbar-nav
     [:li {:class (when (= home (:page @state)) "active")}
      [:a {:on-click #(secretary/dispatch! "#/")} "Dashboard"]]

     [:li {:class (when (= add-entry (:page @state)) "active")}
      [:a {:on-click #(secretary/dispatch! "#/add-entry")} "Lisää"]]

     [:li {:class (when (= edit-entry (:page @state)) "active")}
      [:a {:on-click #(secretary/dispatch! "#/entry/1")} "Muokkaa"]]

     [:li {:class (when (= history (:page @state)) "active")}
      [:a {:on-click #(secretary/dispatch! "#/history")} "Historia"]]

     [:li {:class (when (= about (:page @state)) "active")}
      [:a {:on-click #(secretary/dispatch! "#/about")} "Tietoja"]]]]])

(defn text-input [id label]
  (row label [:input.form-control {:field :text :id id}]))

(defn selection-list [label id & items]
  (row label
       [:div.btn-group {:field :single-select :id id}
        (for [[k label] items]
          [:button.btn.btn-default {:key k} label])]))

(def form
  [:div
   [:h1 "Lisää uusi merkintä"]
   (text-input :date "Päivämäärä")
   (selection-list "Merkinnän tyyppi" :type
                   [:type-a "Tyyppi A"]
                   [:type-b "Tyyppi B"]
                   [:type-c "Tyyppi C"])
   (text-input :value "Arvo")])

(defn save-entry [new-entry]
  (fn []
    (POST (str js/context "/entry")
          {:params {:new-entry @new-entry}
           :handler (fn [resp]
                      (.log js/console (str "Response status: " (:status resp)))
                      (swap! state assoc :saved? true))})))

(defn about []
  [:div
   [:h1 "Tietoja sovelluksesta"]
   [:p "todo"]])

(defn history []
  [:div
   [:h1 "Historia"]
   [:p "Add cool NVD3 diagrams and stuff"]]
  )

(defn add-entry []
  (let [new-entry (atom {})]
    (fn []
      [:div
       [bind-fields form new-entry
        (fn [_ _ _] (swap! state assoc :saved? false) nil)]
       (if (:saved? @state)
         [:p "Saved"]
         [:button {:type "submit"
                   :class "btn btn-success"
                   :onClick (save-entry new-entry)}
          "Tallenna"])])))

(defn edit-entry [entry-id]
  [:div
   [:h1 (str "Muokkaa merkintää " (@state :entry-id))]
   [:button
    {:class "btn btn-lg btn-success"
     :on-click #(secretary/dispatch! "#/")} "Tallenna"]
   [:button
    {:class "btn btn-lg btn-warning"
     :on-click #(secretary/dispatch! "#/")} "Peruuta"]
   [:button
    {:class "btn btn-lg btn-danger"
     :on-click #(js/alert "TODO: remove")} "Poista"]
   ]
  )

(defn home []
  [:div
   [:h1 "Dashboard"]
   [:button
    {:class "btn btn-lg btn-success"
     :on-click #(secretary/dispatch! "#/add-entry")} "Lisää uusi merkintä"]
   [:h2 "Entry list"]
   [:ul

    (for [item (@state :data)]
      ^{:key item} [:li [:a {:on-click #(secretary/dispatch! (str "#/entry/" (item :id)))}
                         (str "Item: " (item :id) " Date: " (item :date))]])]])

(defn page []
  [(:page @state)])

(secretary/set-config! :prefix "#")

(defn put! [k v]
  (swap! state assoc k v))

(defroute "/" []
  (swap! state assoc :page home :bread nil)
  )

(defroute "/entry/:id" {:as params}
  (swap! state assoc :page edit-entry :entry-id (params :id) :bread "Muokkaa merkintää"))

(defroute "/add-entry" []
  (.log js/console "Add entry-view")
  (swap! state assoc :page add-entry :bread "Lisää merkintä"))

(defroute "/history" []
  (.log js/console "History-view")
  (swap! state assoc :page history :bread "Historia"))

(defroute "/about" []
  (swap! state assoc :page about :bread "Tietoja"))

(defn init! []
  (swap! state assoc :page home)
  (GET "/entries" {:handler #(swap! state assoc :data (% :data))})

  (reagent/render-component [breadcrumbs] (.getElementById js/document "navbar"))
  (reagent/render-component [page] (.getElementById js/document "app")))
