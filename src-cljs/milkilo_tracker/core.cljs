;; Entry point, setup routings
(ns milkilo-tracker.core
  (:require
   [cljs.test :as t]
   [milkilo-tracker.session :as session]
   [reagent.core :as reagent :refer [atom]]

   [milkilo-tracker.utils :refer [log redirect!]]
   [figwheel.client :as fw]

   [secretary.core :as secretary
    :refer [dispatch!]
    :refer-macros [defroute]]
   
   [milkilo-tracker.history :as history]
   [ajax.core :refer [GET]]
   
   [milkilo-tracker.pages.dashboard  :refer [dashboard-page]]
   [milkilo-tracker.pages.add        :refer [add-entry-page]]
   [milkilo-tracker.pages.edit       :refer [edit-entry-page]]
   [milkilo-tracker.pages.history    :refer [history-page]]
   [milkilo-tracker.pages.about      :refer [about-page]]
   [milkilo-tracker.pages.components :refer [breadcrumbs]]
   ))

(defroute dashboard-path "/" []
  (session/put! :selected-entry nil)
  (log "Core: going to dashboard")
  (session/put! :current-page dashboard-page)
  (session/put! :bread nil))

(defroute edit-entry-path "/edit-entry/:id" {:as params}
  (.log js/console "Params are")
  (log params)
  (let [entries (session/get :entries)
        entry (first (filter #(= (int (params :id)) (:id %)) entries))]
    (session/put! :selected-entry entry)
    (log entry))
  
  (session/put! :current-page add-entry-page)
  (session/put! :bread "Muokkaa merkintää"))

(defroute add-entry-path "/add-entry" []
  (session/put! :current-page add-entry-page)
  (session/put! :bread "Lisää merkintä"))

(defroute history-path "/history" []
  (session/put! :current-page history-page)
  (session/put! :bread "Historia"))

(defroute about-path "/about" []
  (session/put! :current-page about-page)
  (session/put! :bread "Tietoja sovelluksesta"))

(def current-page
  (atom nil))

(defn page []
  [(session/get :current-page)])

(defn init-data-handler [resp]
  "Receive initial user data from server and put it to session."
  ;; TODO Handle multiple user sites etc.
  ;;(.log js/console (str resp))
  (let [site-data (first resp)]
    (session/put! :entries (site-data :entries))
    (session/put! :site (dissoc site-data :entries))))

(defn render-view []
  (reagent/render-component [page] (.getElementById js/document "app"))
  (reagent/render-component [breadcrumbs] (.getElementById js/document "navbar"))
  )

(defn init! []
  "Initial application state"
  (js/console.log "(core/init!)")
  (enable-console-print!)
  
  (.initializeTouchEvents js/React true)
  
  (GET "/entries" {:handler init-data-handler})

  (secretary/set-config! :prefix "#")
  (history/hook-browser-navigation!)

  (session/put! :bread nil)
  (render-view)
  ;; Run unit tests during development.
  ;; TODO if dev flag set?
  ;; (t/run-tests 'milkilo-tracker.pages.add)
  )

;; Development utilities 
(fw/start {:websocket-url "ws://localhost:3449/figwheel-ws"
           :on-jsload (fn []
                        ;; TODO setup to work without page refresh
                        ;;(redirect! "/#/")
                        ;;(render-view)

                        (redirect! "/")
                        )})
