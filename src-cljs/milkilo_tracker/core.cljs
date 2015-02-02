;; Entry point, setup routings
(ns milkilo-tracker.core
  (:require
   [milkilo-tracker.session :as session]
   [milkilo-tracker.utils :refer [log]]
   
   [milkilo-tracker.pages.dashboard  :refer [dashboard-page]]
   [milkilo-tracker.pages.add        :refer [add-entry-page]]
   [milkilo-tracker.pages.edit       :refer [edit-entry-page]]
   [milkilo-tracker.pages.history    :refer [history-page]]
   [milkilo-tracker.pages.about      :refer [about-page]]
   [milkilo-tracker.pages.components :refer [breadcrumbs]]

   [reagent.core :as reagent :refer [atom]]
   
   [ajax.core :refer [GET]]
   [figwheel.client :as fw]

   [goog.events :as events]
   [goog.history.EventType :as EventType]
   
   [secretary.core :as secretary :refer-macros [defroute]]
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
  (session/put! :bread nil)
  )

(defroute "/entry/:id" {:as params}
  (session/put! :current-page edit-entry-page)
  (session/put! :bread "Muokkaa merkintää")
  )

(defroute "/add-entry" []
  (session/put! :current-page add-entry-page)
  (session/put! :bread "Lisää merkintä")
  )

(defroute "/history" []
  (session/put! :current-page history-page)
  (session/put! :bread "Historia")
  )

(defroute "/about" []
  (session/put! :current-page about-page)
  (session/put! :bread "Tietoja sovelluksesta")
)

(def current-page
  (atom nil)
  )

(defn page []
  [(session/get :current-page)]
  )

(defn init! []
  (js/console.log "(init!)")
  (enable-console-print!)
  (secretary/set-config! :prefix "#")
  
  (session/put! :current-page dashboard-page)
  (session/put! :bread nil)
  (GET "/entries" {:handler #(session/put! :data (% :data))})

  (reagent/render-component [page] (.getElementById js/document "app"))
  (reagent/render-component [breadcrumbs] (.getElementById js/document "navbar"))
  )

(fw/start {:websocket-url "ws://localhost:3449/figwheel-ws"
           :on-jsload (fn [] (init!))})

(init!)
