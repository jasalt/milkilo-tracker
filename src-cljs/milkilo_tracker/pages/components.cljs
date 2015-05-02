;; Some common components
(ns milkilo-tracker.pages.components
  (:require
   [milkilo-tracker.session :as session]
   [reagent.core :as reagent :refer [atom]]
   [secretary.core :refer [dispatch!]]
   [milkilo-tracker.utils :refer [get-cookie]]))

(defn breadcrumbs []
  [:ol.breadcrumb
   [:li.active
    ;; TODO deactivate link at #/
    [:a {:href "#/"} "Dashboard"]]
   (if-let [bread (session/get :bread)]
     [:li.active bread])])

(defn cancel []
  [:a.btn.btn-lg.btn-cancel.btn-danger.btn-block.top-margin
   {:href "#/"} "Takaisin"]
  )

(defn row [label & body]
  [:div.row.top-margin
   [:div.col-md-2 [:span.span-lg (str label ":")]]
   [:div.col-md-3 body]])

(defn date-input []
  [:div.input-lg {:field :datepicker :id :entry.date
                  :date-format "dd/mm/yyyy" :inline true :auto-close? true}])

(def entry-field
  [:div
   [row "Mittausarvo"]
   [:input.form-control.input-lg
    {:field :text :id :entry.value}]]
  )

(def entry-type-selector
  [:div
   (let [options
         ;; Convert map into array for (for)
         (map (fn [entry-type]
                (let [table-name (first entry-type)
                      value-map (second entry-type)]
                  (assoc value-map :table table-name)))
              (session/get :entry-types))]

     [row "Merkintätyyppi"
      [:select {:field :list :id :entry.type}
       (for [type options] ^{:key (type :table)}
            [:option {:key (type :table)} (type :name)])]])
   [:br]])
