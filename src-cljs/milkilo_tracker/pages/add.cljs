(ns milkilo-tracker.pages.add
  (:require
   [milkilo-tracker.session :as session]
   [reagent.core :as reagent :refer [atom]]
   [reagent-forms.core :refer [bind-fields]]
   [ajax.core :refer [POST, GET]]
   [milkilo-tracker.pages.components :refer [text-input date-input
                                             cancel row]]
   [milkilo-tracker.utils :refer [log]]
   [cljs-time.local :refer [local-now]]
   [cljs-time.core :refer [day month year]]
   ))

(def initial-entry
  (let [now (local-now)]
    {:entry
     {:date {:year (year now),
             :month (month now),
             :day (day now)}
      :type nil
      :value nil}}
    ))

(defn save-entry [new-entry]
  (fn []
    (POST (str js/context "/entry")
          {:params {:new-entry @new-entry}
           :handler (fn [resp]
                      (.log js/console (str "Response status: "
                                            (:status resp)))
                      (session/put! :saved? true))})))

(def entry-form
  [:div
   [row "Päivämäärä"
    (date-input)]

   [row "Merkintätyyppi"
    (let [entry-types (session/get :entry-types)]
      [:select {:field :list :id :entry.type}
       (for [type entry-types]
         ^{:key (type :table)}
         [:option {:key (type :table)}
          (type :name)])
       ])]

   (text-input :value "Arvo")

   ])

(defn add-entry-page []
  (let [new-entry (atom initial-entry)]
    (fn []
      [:div
       [bind-fields entry-form new-entry
        (fn [_ _ _] (session/put! :saved? false) nil)

        ;; Bind to change on entry type
        (fn [id value doc]
          (when (= id '(:entry :type))
            (log (str doc))
            (log id)
            (log value)
            (assoc-in doc [:entry :type] value)
            )
          )
        ]

       (if (session/get :saved?)
         [:p "Saved"]

         [:button.btn.btn-success.btn-lg.btn-block.top-margin
          {:type "submit"
           :onClick (save-entry new-entry)}
          "Tallenna"])
       [cancel]
       ])
    ))
