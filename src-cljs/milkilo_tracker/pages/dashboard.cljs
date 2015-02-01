(ns milkilo-tracker.pages.dashboard
  (:require
   ;;[milkilo-tracker.core :refer [state]] ;; Has session/state for now
   [milkilo-tracker.session :as session]

   [reagent.core :as reagent :refer [atom]]
   ;;[reagent-forms.core :refer [bind-fields]]
   [secretary.core :refer [dispatch!]]
   ;; [secretary.core :as secretary :include-macros true :refer [defroute]]
   ))

(defn dashboard []
  [:div
   ;;(str @c/state)
   "asdf"
   ])

;; (defn dashboard []
;;   [:div
;;    [:button.btn.btn-lg.btn-primary.btn-block
;;     {:on-click #(dispatch! "#/add-entry")} "Lisää uusi merkintä"]
;;    [:div.chart-container
;;     [:p "Diagram:"]
;;     [:img.img-responsive
;;      {:src "http://placekitten.com.s3.amazonaws.com/homepage-samples/200/138.jpg"
;;       :on-click #(dispatch! "#/history")}]]

;;    (if-let [last-entry (first (@c/state :data))]
;;      [:div
;;       [:p "Viimeisin mittaus"]
;;       [:a {:on-click #(dispatch! (str "#/entry/" (last-entry :id)))}
;;        (str "Item: " (last-entry :id) " Date: " (last-entry :date)) ]]

;;      [:p "Ei mittauksia.."])])
