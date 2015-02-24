(ns milkilo-tracker.db.core
  (:use korma.core
        [korma.db :only (defdb)])
  (:require [milkilo-tracker.db.schema :as schema]))

;; Connect and create connection pool
(defdb db schema/db-spec)

;; Represents users table
(defentity users)

(defn create-user [user]
  (insert users
          (values user)))

(defn update-user [id first-name last-name email]
  (update users
  (set-fields {:first_name first-name
               :last_name last-name
               :email email})
  (where {:id id})))

(defn get-user [id]
  (first (select users
                 (where {:id id})
                 (limit 1))))
