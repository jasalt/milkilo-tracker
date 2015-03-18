(ns milkilo-tracker.routes.home
  (:require [milkilo-tracker.layout :as layout]
            [milkilo-tracker.util :as util]
            [compojure.core :refer :all]
            [noir.response :refer [edn, redirect]]
            [noir.cookies :as cookies]
            [cemerick.friend :as friend]
            [clojure.pprint :refer [pprint]]
            [milkilo-tracker.db.core :as db]
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
  (pprint "Fetching initial user data")
  (let [user-id (:id (friend/current-authentication))]
    (db/get-user-data user-id)))

(defn save-entry [entry]
  (let [user-id (:id (friend/current-authentication))
        user-sites (->> (db/get-administered-sites user-id)
                       (map #(first (vals (select-keys % [:id]))))
                       vec)]
    ;; Check users access for site
    (./pprint entry)
    (./pprint user-sites)
    (if (some #(= (entry :site_id ) %) user-sites)
      (./pprint "JOOO")
      (./pprint "EI Lupaa")
      )
    )
  {:status "ok"}  ;; TODO respond status
  )

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/entries" [] (edn (get-entries)))
  (POST "/entry" {:keys [body-params]} (edn (save-entry body-params)))
  )
