(ns milkilo-tracker.pages.add-entry
  (:require
   [milkilo-tracker.components :refer [text-input selection-buttons cancel]]
   [reagent.core :as reagent :refer [atom]]
   [reagent-forms.core :refer [bind-fields]]
   [ajax.core :refer [POST, GET]]
   ;;[secretary.core :refer [dispatch!]]
   ;; [secretary.core :as secretary :include-macros true :refer [defroute]]

   ))

(def form
  [:div
   (text-input :date "Päivämäärä")
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
                      (swap! state assoc :saved? true))})))

(defn add-entry []
  (let [new-entry (atom {})]
    (fn []
      [:div
       [bind-fields form new-entry
        (fn [_ _ _] (swap! state assoc :saved? false) nil)]
       (if (:saved? @state)
         [:p "Saved"]
         [:button.btn.btn-success.btn-lg.btn-block.top-margin {:type "submit"
                                                               :onClick (save-entry new-entry)}
          "Tallenna"])
       [cancel]
       ])))
