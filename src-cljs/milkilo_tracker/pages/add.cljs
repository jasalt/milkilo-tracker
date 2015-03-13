(ns milkilo-tracker.pages.add
  (:require
   [milkilo-tracker.session :as session]
   [reagent.core :as reagent :refer [atom]]
   [reagent-forms.core :refer [bind-fields]]
   [ajax.core :refer [POST, GET]]
   [milkilo-tracker.pages.components :refer [date-input
                                             cancel row]]
   [milkilo-tracker.utils :refer [log]]
   [cljs-time.local :refer [local-now]]
   [cljs-time.core :refer [day month year]]))

(def initial-entry
  (let [now (local-now)]
    {:entry
     {:date {:year (year now),
             :month (month now),
             :day (day now)}
      :type nil
      :value nil}}))

(defn save-entry [new-entry]
  (fn []
    (if (.confirm js/window (str "Tallenna mittaus "
                                 ((@new-entry :entry) :value)))
      
      (POST (str js/context "/entry")
            {:params {:new-entry @new-entry}
             :handler (fn [resp]
                        (.log js/console (str "Response status: "
                                              (:status resp)))
                        (session/put! :saved? true))}))))

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

(def entry-field
  [:div
   [row "Mittausarvo"]
   [:input.form-control.input-lg
    {:field :text :id :entry.value}]]
  )

(defn add-entry-page []
  (let [new-entry (atom initial-entry)
        entry-types (session/get :entry-types)]
    (fn []
      [:div


       [bind-fields [:div
                     [row "Päivämäärä"
                      (date-input)]
                     entry-type-selector
                     entry-field
                     ]
        new-entry
        ;; Any change made will falsify :saved?
        (fn [_ _ _]
          (session/put! :saved? false)
          nil)
        ;; Bind to entry type change
        (fn [id value doc]
          (when (= id '(:entry :type))
            (log value)
            (session/put! :current-entry-type value)
            (assoc-in doc [:entry :type] value)
            )
          )]

       (let [current-type ((@new-entry :entry) :type)
             current-entry-info (entry-types current-type)
             description (:description current-entry-info)
             unit (:unit current-entry-info)
             ]
         [row "Selite"
          [:div.col-xs-6 [:h3 description]]
          [:div.col-xs-6 [:h2.pull-right (str "Yksikkö: " unit)]]])

       ;; Submit button
       (if (session/get :saved?)
         [:h1 "Tallennettu!"]
         [:button.btn.btn-success.btn-lg.btn-block.top-margin
          {:type "submit"
           :onClick (save-entry new-entry)}
          "Tallenna"])
       [cancel]
       [:p (str "testaus-tieto: " @new-entry)]
       ]
      )))
