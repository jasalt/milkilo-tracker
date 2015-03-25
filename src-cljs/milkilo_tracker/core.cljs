;; Entry point, setup routings
(ns milkilo-tracker.core
  (:require
   [cljs.test :as t]
   [milkilo-tracker.session :as session]
   [reagent.core :as reagent :refer [atom]]
   
   [milkilo-tracker.utils :refer [log]]
   [figwheel.client :as fw]
   
   [secretary.core :as secretary :refer-macros [defroute]]
   [ajax.core :refer [GET]]
   [goog.events :as events]
   [goog.history.EventType :as EventType]

   [milkilo-tracker.pages.dashboard  :refer [dashboard-page]]
   [milkilo-tracker.pages.add        :refer [add-entry-page]]
   [milkilo-tracker.pages.edit       :refer [edit-entry-page]]
   [milkilo-tracker.pages.history    :refer [history-page]]
   [milkilo-tracker.pages.about      :refer [about-page]]
   [milkilo-tracker.pages.components :refer [breadcrumbs]]
   ))

;; TODO fix
;; http://squirrel.pl/blog/tag/clojurescript/
;; (defn hook-browser-navigation! []
;;   (doto (History.)
;;     (events/listen
;;      EventType/NAVIGATE
;;      (fn [event]
;;        (secretary/dispatch! (.-token event))))
;;     (.setEnabled true)))
 

(defroute "/" []
  (session/put! :current-page dashboard-page)
  (session/put! :bread nil))

(defroute "/entry/:id" {:as params}
  (session/put! :current-page edit-entry-page)
  (session/put! :bread "Muokkaa merkintää"))

(defroute "/add-entry" []
  (session/put! :current-page add-entry-page)
  (session/put! :bread "Lisää merkintä"))

(defroute "/history" []
  (session/put! :current-page history-page)
  (session/put! :bread "Historia"))

(defroute "/about" []
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

(defn init! []
  "Initial application state"
  (.initializeTouchEvents js/React true)
  (js/console.log "(core/init!)")
  (enable-console-print!)
  (secretary/set-config! :prefix "#")
  (GET "/entries" {:handler init-data-handler})
  
  (session/put! :current-page dashboard-page)
  (session/put! :bread nil)
  (reagent/render-component [page] (.getElementById js/document "app"))
  (reagent/render-component [breadcrumbs] (.getElementById js/document "navbar"))

  ;; Run unit tests during development.
  (t/run-tests 'milkilo-tracker.pages.add)
  )

;; Development utilities
(fw/start {:websocket-url "ws://localhost:3449/figwheel-ws"
           :on-jsload (fn []
                        (init!))})

