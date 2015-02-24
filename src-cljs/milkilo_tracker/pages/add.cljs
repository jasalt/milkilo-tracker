(ns milkilo-tracker.pages.add
  (:require
   [milkilo-tracker.session :as session]
   [reagent.core :as reagent :refer [atom]]
   [reagent-forms.core :refer [bind-fields]]
   [ajax.core :refer [POST, GET]]
   [milkilo-tracker.pages.components :refer [text-input date-input cancel row]]
   ))

(defn selection-buttons [label id & items]
  (row label
       [:div.text-center.top-margin {:field :single-select :id id}
        (for [[k label] items]
          [:button.btn.btn-default.btn-lg {:key k} label])]))

(def form
  [:div
   (date-input)
   (selection-buttons "Merkinnän tyyppi" :type
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
                      (session/put! :saved? true))})))

(defn add-entry-page []
  (let [new-entry (atom {})]
    (fn []
      [:div
       [bind-fields form new-entry
        (fn [_ _ _] (session/put! :saved? false) nil)]
       (if (session/get :saved?)
         [:p "Saved"]
         [:button.btn.btn-success.btn-lg.btn-block.top-margin {:type "submit"
                                                               :onClick (save-entry new-entry)}
          "Tallenna"])
       [cancel]
       ])))
