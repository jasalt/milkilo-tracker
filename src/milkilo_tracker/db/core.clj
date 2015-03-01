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

;; (get-entries 2)
;; (defn get-entries [site-id]
;;   (select entries
;;           (where {:id site-id}))
;;   )

(defn get-entries [site-id]
  [1 2 3 4] ;; TODO)

(defn get-user-data [user-id]
  (let [user-sites (get-administered-sites user-id)
        cleaned-user-sites (map #(select-keys % [:id :name]) user-sites)]
    (map (fn [site] (merge site {:entries (get-entries (site :id))}))
         cleaned-user-sites)))
