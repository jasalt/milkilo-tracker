(ns milkilo-tracker.routes.home
  (:require [milkilo-tracker.layout :as layout]
            [milkilo-tracker.util :as util]
            [compojure.core :refer :all]
            [noir.response :refer [edn, redirect]]
            [noir.cookies :as cookies]
            [cemerick.friend :as friend]
            [clojure.pprint :refer [pprint]]
            [milkilo-tracker.db.core :as db]
            [clj-time.core :as t]
            [clj-time.coerce :as tc]
            ))

;; TODO Hackfixed authentication exception handling
;; Why friends default unauth-handler does not catch error and redirect?
(defn home-page []
  (try
    (friend/authenticated
     (do
       (let [email (str (:email (friend/current-authentication)))]
         (println (str "Logged in as: " email))
         (cookies/put! "email" email))
       (layout/render "app.html")))
    (catch Exception e
      (do
        (println (str "!! Caught exception: " (.getMessage e)))
        (redirect "/login")))))

;; TODO rename to get-sites
(defn get-entries []
  (println "Fetching initial user data")
  (let [user-id (:id (friend/current-authentication))]
    (db/get-user-data user-id)))

(defn user-has-access [entry]
  (let [user-id (:id (friend/current-authentication))
        user-sites (->> (db/get-administered-sites user-id)
                        (map #(first (vals (select-keys % [:id]))))
                        vec)]
    (if (some #(= (entry :site_id ) %) user-sites)
      true
      nil
      )
    )
  )

(defn save-entry [entry]
  "Save entry or update existing one with id if user has access."
  (println "Try insert/update")
  (println entry)
  (if (user-has-access entry)
    (if (entry :id)
      (do
        (println "Updating:")
        (pprint entry)
        (db/update-entry entry))
      (do
        (println "Inserting:")
        (pprint entry)
        (db/insert-entry entry)))
    (do
      (println "Unauthorized")
      {:status 500})))

(defn delete-entry [entry]
  (println "Try delete")
  (println entry)
  (if (user-has-access entry)
    (do
      (println "Deleting:")
      (pprint entry)
      (db/delete-entry entry))
    (do
      (println "Unauthorized")
      {:status 500})))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/entries" [] (edn (get-entries)))
  (POST "/entry" {:keys [body-params]} (edn (save-entry body-params)))
  (DELETE "/entry" {:keys [body-params]} (edn (delete-entry body-params)))
  )
