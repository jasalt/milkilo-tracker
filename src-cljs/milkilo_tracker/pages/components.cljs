;; Some common components
(ns milkilo-tracker.pages.components
  (:require
   [milkilo-tracker.session :as session]
   [reagent.core :as reagent :refer [atom]]
   [secretary.core :refer [dispatch!]]
   [milkilo-tracker.utils :refer [get-cookie]]
   )
  )

(defn breadcrumbs []
  [:ol.breadcrumb
   [:li.active
    ;; TODO deactivate link at #/
    [:a {:on-click #(dispatch! "#/")} "Dashboard"]]
   (if-let [bread (session/get :bread)]
     [:li.active bread])
   
   ])

(defn cancel []
  [:button.btn.btn-lg.btn-cancel.btn-danger.btn-block.top-margin
   {:on-click #(dispatch! "#/")} "Peruuta"]
  )

;; (defn big-button [color action]
;;   ;;TODO
;;   nil
;;   )

(defn row [label & body]
  [:div.row.top-margin
   [:div.col-md-2 [:span.span-lg (str label ":")]]
   [:div.col-md-3 body]])

(defn text-input [id label]
  (row label
       [:input.form-control.input-lg {:field :text :id id}]
       ))

(defn date-input []
  (row "Päivämäärä"
       [:div.input-lg {:field :datepicker :id :entry-date :date-format "dd/mm/yyyy"
                       :inline true :auto-close? true}]))
