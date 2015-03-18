(ns milkilo-tracker.pages.add
  (:require
   [milkilo-tracker.session :as session]
   [reagent.core :as reagent :refer [atom]]
   [reagent-forms.core :refer [bind-fields]]
   [ajax.core :refer [POST, GET]]
   [milkilo-tracker.pages.components :refer [date-input
                                             cancel row
                                             entry-field
                                             entry-type-selector]]
   [milkilo-tracker.utils :refer [log]]
   [cljs-time.local :refer [local-now]]
   [cljs-time.core :refer [day month year]]))

(def initial-entry
  ;; Initialize new entry with current date
  (let [now (local-now)]
    {:entry
     {:date {:year (year now),
             :month (month now),
             :day (day now)}
      :type nil
      :value nil}}))

(defn get-entry-info [entry-key]
  ;; Helper function that returns entry type information of the given entry key
  (let [entry-types (session/get :entry-types)]
    (entry-types entry-key)
    )
  )

(defn validate-entry [entry]
  ;; Input validation
  (log (str "validating " entry))
  
  ;; {:entry {:date {:year 2015, :month 3, :day 13}, :type :comment, :value "3341"}}
  (let [entry-map (entry :entry)
        value (entry-map :value)
        date (entry-map :date)
        type (entry-map :type)
        input-type ((get-entry-info type) :input-type)]
    (if value
      (do
        (log input-type)
        (case input-type
          :numeric (if (and (<= value 1000) (>= value 0))
                     true (.alert js/window "Virheellinen numeroarvo. Anna lukema väliltä 0-1000"))
          ;; Text length between 1-1001
          :text (if (or (clojure.string/blank? value) (> (.-length value) 1000))
                  (.alert js/window "Virheellinen teksti. Kirjaimia saa olla väliltä 1-1000.")
                  true
                  )
          nil
          )
        )
      (do
        (.alert js/window "Mittausarvo puuttuu!")
        nil)
      )
    ))

(defn save-entry [new-entry]
  (fn []
    (if (and (validate-entry @new-entry)
             (.confirm js/window (str "Tallenna mittaus "
                                      ((@new-entry :entry) :value)))
             )

      (POST (str js/context "/entry")
            {:params {:new-entry @new-entry}
             :handler (fn [resp]
                        (.log js/console (str "Response status: "
                                              (:status resp)))
                        (session/put! :saved? true))}))))

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

       (let [current-entry-info (get-entry-info ((@new-entry :entry) :type))
             description (:description current-entry-info)
             unit (:unit current-entry-info)]
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
       ])))
