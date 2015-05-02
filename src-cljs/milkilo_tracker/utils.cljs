(ns milkilo-tracker.utils
  (:require
   [reagent.core :as reagent :refer [atom]]
   [ajax.core :refer [GET, DELETE]]
   [secretary.core :refer [dispatch!]]
   [milkilo-tracker.session :as session]
   [goog.net.cookies]
   ))

(defn get-cookie [key]
  (.get goog.net.cookies (name key))
  )

(defn log
  ([val desc]
  (js/console.log desc)
  (js/console.log (pr-str val)))
  ([val] (log val "Logging:")))

(defn logout []
  (GET "/logout") ;; Response drops session cookie
  (.replace js/location "/login") ;; TODO hacky way to redirect client after logout
  )

(defn get-entry-info [entry-key]
  ;; Helper function that returns entry type information of the given entry key
  (let [entry-types (session/get :entry-types)]
    (entry-types entry-key)))

(defn delete-entry [this-entry]
  (fn []
    (if (.confirm js/window "Haluatko varmasti poistaa?")
      (DELETE
       (str js/context "/entry")
       {:params this-entry ;;(@this-entry :entry)
        :handler
        (fn [resp]
          (.log js/console (str "Response: " resp))
          ;; Remove deleted entry
          (session/update-in!
           [:entries]
           (fn [all-entries resp]
             (remove #(= (dissoc resp :site_id) %) all-entries)) resp)
          (dispatch! "#/"))}))))
