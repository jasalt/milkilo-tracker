(ns milkilo-tracker.db.core
  (:use korma.core
        [korma.db :only (defdb)])
  (:require [milkilo-tracker.db.schema :as schema]))

;; Create connection pool
(defdb db schema/db-spec)

;; Represents users table
(defentity users)
(defentity sites)
(defentity entries)

(defn create-user [user]
  (insert users
          (values user)))

(defn update-user [id first-name last-name email]
  (update users
          (set-fields {:first_name first-name
                       :last_name last-name
                       :email email})
          (where {:id id})))

(defn get-user [user]
  (first (select users
                 (where user) ; {:id ID} or {:username EMAIL}
                 (limit 1))))

(defn get-administered-sites [user-id]
  ;; Return all sites where user is admin
  (exec-raw [(str "SELECT * FROM sites WHERE admins @> ARRAY["user-id"]")]
            :results))

(defn get-entries [site-id]
  (map #(dissoc % :site_id)
       (select entries
               (where {:site_id site-id}))))

(defn get-user-data [user-id]
  (let [user-sites (get-administered-sites user-id)
        cleaned-user-sites (map #(select-keys % [:id :name]) user-sites)]
    (map
     (fn [site]
       (merge site {:entries (get-entries (site :id))}))
     cleaned-user-sites)))

;;(get-user-data 15)
