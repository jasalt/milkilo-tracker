(ns milkilo-tracker.pages.dashboard
  (:require
   [milkilo-tracker.session :as session]
   [reagent.core :as reagent :refer [atom]]
   [secretary.core :as secretary]
   [clojure.string :as str]
   [milkilo-tracker.utils :refer [get-cookie log logout
                                  get-entry-info
                                  delete-entry]]))

(defn mount-graph []
  (log "Mounting graph!")
  (.addGraph
   js/nv
   (fn []
     (let
         [chart
          (.. js/nv -models lineChart
              (margin #js {:left 20})
              (useInteractiveGuideline true)
              (transitionDuration 350)
              (showLegend true)
              (showYAxis true)
              (showXAxis true))]
       (.. chart -xAxis
           (axisLabel "x-axis")
           (tickFormat (.format js/d3 ",r")))
       (.. chart -yAxis
           (axisLabel "y-axis")
           (tickFormat (.format js/d3 ",r")))

       (let [my-data [{:x 1 :y 5} {:x 2 :y 3} {:x 3 :y 4} {:x 4 :y 1} {:x 5 :y 2}]]

         (.. js/d3 (select "#d3-node svg")
             (datum (clj->js [{:values my-data
                               :key "my-red-line"
                               :color "red"
                               }]))
             (call chart))))))
  )

(def dashboard-page-did-mount
  (with-meta identity
    {:component-did-mount #(do
                             (.log js/console "dashboard-did-mount")
                             (mount-graph)
                             )
     :component-did-update #(do
                              (.log js/console "dashboard-did-update")
                             (mount-graph)
                             )}))

(defn dashboard-page []
  (fn []
    [:div
     [:br]
     [:a.btn.btn-lg.btn-primary.btn-block
      {:href "#/add-entry"} "Uusi merkintä"]
     [:br]
     (if-let [entries (session/get :entries)]
       ;; TODO show a couple last entries when CLJS subvec reverse bug is fixed.
       (let [last-entries entries
             entry-types (session/get :entry-types)]
         [:div
          ;; [:div.well.well-sm
          ;;  [:div#d3-node {:style {:width "400" :height "200"}} [:svg ]]]
          [:div.panel.panel-default
           [:div.panel-heading
            [:h4 "Edelliset merkinnät"]]
           [:div.list-group
            (doall
             (for [entry last-entries]
               ^{:key entry}
               (let [title (:name (entry-types (entry :type)))
                     date (:date entry)]
                 [:div.list-group-item
                  [:div.clearfix {:style {:clear "both"}}
                   [:span.badge.pull-left {:style {:font-size "1.3em"}} (str title " ")]
                   [:button.btn.btn-danger.pull-right
                    {:onClick (delete-entry (assoc entry :site_id ((session/get :site) :id)))} "X"]]
                  [:p (str (date :day)"."(date :month)"."(date :year)) " - "[:strong (str (entry :value))]]])))]]]))

     (if-let [site (session/get :site)]
       [:div
        [:h4 "Puhdistamo (testiarvo)"]
        [:p (str site)]]
       [:p "Puhdistamo puuttuu (tai sen tietoja ladataan)"])

     (let [before-at (first (str/split (get-cookie "email") "%40"))
           name (str (str/join " " (map str/capitalize (str/split before-at "."))))]
       [:a.text-center {:on-click #(let [message "Oletko varma että haluat kirjautua ulos?"
                                         dialog-result (.confirm js/window message)]
                                     (when dialog-result (logout)))}
        [:h4 [:small (str "Kirjaudu ulos ("name")")]]])]))
