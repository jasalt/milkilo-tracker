;; Entry point, setup routings
(ns milkilo-tracker.core
  (:require
   [milkilo-tracker.session :as session]
   ;;[milkilo-tracker.utils :refer [log]]
   [milkilo-tracker.pages.dashboard :refer [dashboard]]
   ;;[milkilo-tracker.pages.add-entry :refer [add-entry]]
   ;;[milkilo-tracker.pages.edit-entry :refer [edit-entry]]
   ;;[milkilo-tracker.pages.history :refer [history]]
   ;;[milkilo-tracker.pages.about :refer [about]]

   [milkilo-tracker.pages.components :refer [breadcrumbs]]

   [reagent.core :as reagent :refer [atom]]
   ;;[reagent-forms.core :refer [bind-fields]]
   [ajax.core :refer [GET]]
   [figwheel.client :as fw]

   [goog.events :as events]
   [goog.history.EventType :as EventType]
   
   [secretary.core :as secretary :refer-macros [defroute]]
   ;;[secretary.core :as secretary]
   ;; [secretary.core :as secretary :include-macros true :refer [defroute]]
   )
  ;;(:require-macros [secretary.core :refer [defroute]])
  )

;; (defn hook-browser-navigation! []
;;   (doto (History.)
;;     (events/listen
;;      EventType/NAVIGATE
;;      (fn [event]
;;        (secretary/dispatch! (.-token event))))
;;     (.setEnabled true)))


(defroute "/" []
  ;;(swap! state assoc :page dashboard :bread nil)
  (session/put! :current-page dashboard)
  (session/put! :current-page nil)
  )

;; (defroute "/entry/:id" {:as params}
;;   (swap! state assoc :page edit-entry :entry-id (params :id) :bread "Muokkaa merkintää"))
;; (defroute "/add-entry" []
;;   (.log js/console "Add entry-view")
;;   (swap! state assoc :page add-entry :bread "Lisää merkintä"))
;; (defroute "/history" []
;;   (.log js/console "History-view")
;;   (swap! state assoc :page history :bread "Historia"))
;; (defroute "/about" []
;;   (swap! state assoc :page about :bread "Tietoja"))

(def current-page
  (atom nil)
  )

(defn page []
  [(session/get :current-page)]
  )

;; (defn render-stuff []
;;   ;;(reagent/render-component [breadcrumbs] (.getElementById js/document "navbar"))
;;   (reagent/render-component [page] (.getElementById js/document "app"))
;;   )


(defn init! []
  (js/console.log "(init!)")
  (secretary/set-config! :prefix "#")
  ;; (enable-console-print!)
  
  ;;(swap! state assoc :page dashboard-page)
  (session/put! :current-page dashboard)
  (session/put! :bread nil)
  (GET "/entries" {:handler #(session/put! :data (% :data))})
  ;;(render-stuff)

  (reagent/render-component [page] (.getElementById js/document "app"))
  (reagent/render-component [breadcrumbs] (.getElementById js/document "navbar"))
  )

(fw/start {:websocket-url "ws://localhost:3449/figwheel-ws"
           :on-jsload (fn [] (init!))})

(init!)
