(ns milkilo-tracker.pages.add
  (:require
   [milkilo-tracker.session :as session]
   [reagent.core :as reagent :refer [atom]]
   [reagent-forms.core :refer [bind-fields]]
   [ajax.core :refer [POST, GET]]
   [milkilo-tracker.pages.components :refer [date-input
                                             cancel row
                                             entry-field]]
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
    (POST (str js/context "/entry")
          {:params {:new-entry @new-entry}
           :handler (fn [resp]
                      (.log js/console (str "Response status: "
                                            (:status resp)))
                      (session/put! :saved? true))})))

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

(defn add-entry-page []
  (let [new-entry (atom initial-entry)]
    (fn []
      [:div
       

       [bind-fields [:div
                     [row "Päivämäärä"
                      (date-input)]
                     entry-type-selector
                     [:input.form-control.input-lg
                      {:field :text :id :entry.value}]
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

       [:p (str ((session/get :entry-types) ((@new-entry :entry) :type) ))]
       
       [:p (str "current-doc is " @new-entry)]
       ;; Submit button
       (if (session/get :saved?)
         [:p "Saved"]

         [:button.btn.btn-success.btn-lg.btn-block.top-margin
          {:type "submit"
           :onClick (save-entry new-entry)}
          "Tallenna"])
       [cancel]
       ])))
