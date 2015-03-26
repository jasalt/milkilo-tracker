;; Page for adding entries
;; TODO flatten out unnecessary root level :entry key from data structure
(ns milkilo-tracker.pages.add
  (:require
   [cljs.test :as t]
   [milkilo-tracker.session :as session]
   [reagent.core :as reagent :refer [atom]]
   [reagent-forms.core :refer [bind-fields]]
   [ajax.core :refer [POST, GET]]
   [milkilo-tracker.pages.components :refer [date-input
                                             cancel row
                                             entry-field
                                             entry-type-selector]]
   [milkilo-tracker.utils :refer [log get-entry-info]]
   [cljs-time.local :refer [local-now]]
   [cljs-time.core :refer [day month year]]))

(def initial-entry
  ;; Initialize new entry with current date
  (let [now (local-now)]
    {:entry
     {:date {:year (year now),
             :month (month now),
             :day (day now)}
      :site_id nil
      :type nil
      :value nil}}))

(defn validate-entry [entry]
  "Input validation. Returns true if okay. Else returns map with :error
   containing an error message."
  ;;(log "Validating" entry)
  (let [entry-map (entry :entry)
        value (entry-map :value)
        site-id (entry-map :site_id)
        date (entry-map :date)
        type (entry-map :type)
        entry-info (get-entry-info type)]

    (if-not (and value date type site-id)
      {:error "Mittausarvo puuttuu!"}
      (do
        (case (entry-info :input-type)
          :numeric
          (let [lower-limit (first (entry-info :range))
                upper-limit (second (entry-info :range))]
            (if (or (< value lower-limit) (> value upper-limit))
              {:error (str "Virheellinen numeroarvo. Anna lukema väliltä "
                           lower-limit"-"upper-limit)}
              true))
          :text
          (if (clojure.string/blank? value) ;; (> (.-length value) 1000)
            {:error "Virheellinen teksti. Kirjaimia saa olla väliltä 1-1000."}
            true)
          nil))
      )))

(defn save-entry [new-entry]
  (fn []
    (if-let [validation-error (:error (validate-entry @new-entry))]
      (.alert js/window (str "Virhe syötteessä: " validation-error))
      (and (.confirm js/window (str "Tallenna mittausarvo "
                                    ((@new-entry :entry) :value)))
           (POST (str js/context "/entry")
                 {:params (@new-entry :entry)
                  :handler (fn [resp]
                             (.log js/console (str "Response: "
                                                   resp))
                             (session/update-in! [:entries] conj resp)
                             (session/put! :saved? true))})))))

(defn add-entry-page []
  (let [site-id ((session/get :site) :id) ;; TODO Handle multiple sites
        new-entry (atom (assoc-in initial-entry [:entry :site_id] site-id))
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
            (session/put! :current-entry-type value)
            (assoc-in doc [:entry :type] value)
            )
          )]

       (let [current-entry-info (get-entry-info ((@new-entry :entry) :type))
             description (:description current-entry-info)
             unit (:unit current-entry-info)]
         [:span.span-lg "Selite"]
         [:br]
         [:div.row
          [:div.col-xs-6 [:h4 description]]
          [:div.col-xs-6 [:h4 (str "Yksikkö: " unit)]]
          ])

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

(t/deftest entry-input-validation
  (let [test-entry {:entry
                    {:date
                     {:year 2015, :month 3, :day 26},
                     :site_id 3,
                     :type :comment,
                     :value "Legit value"}}]

    (t/is (nil? (:error (validate-entry test-entry)))
          "String value should be okay.")

    (t/is (nil? (:error (validate-entry
                         (assoc-in test-entry [:entry :value] "  Valid!"))))
          "Beginning whitespace doesn't matter.")

    (t/is (:error (validate-entry (assoc-in test-entry [:entry :value] "  "))
                  "Text value cannot be just whitespace."))

    (t/is (:error (validate-entry (assoc-in test-entry [:entry :value] "")))
          "Empty value is not allowed.")

    (t/is (:error (validate-entry
                   (update-in test-entry [:entry] dissoc :site_id)))
          "Entry without a site_id is not allowed.")

    (t/is (:error (validate-entry
                   (update-in test-entry [:entry] dissoc :date)))
          "Entry without a date is not allowed.")

    (t/is (:error (validate-entry
                   (update-in test-entry [:entry] dissoc :type)))
          "Entry without a type is not allowed.")
    )


  ;; TODO Numeric type must not contain text
  
  ;; Test each numeric entry types values
  (let [entry-types (session/get :entry-types)
        numeric-types (reduce (fn [altered-map [k v]]
                                (when (= (v :input-type) :numeric)
                                  (assoc altered-map k (:range v))))
                              {} entry-types)]
    

    (log numeric-types "Numeric types:")

    ;; TODO test different values
    )
  )
