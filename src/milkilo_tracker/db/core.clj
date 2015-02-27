(ns milkilo-tracker.db.core
  (:use korma.core
        [korma.db :only (defdb)])
  (:require [milkilo-tracker.db.schema :as schema]))

;; Create connection pool
(defdb db schema/db-spec)

;; Represents users table
(defentity users)
(defentity sites)
;;(defentity entries)

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

(def mock-entries
  [
   {:id 1
    :value 2.55
    :date "1.2.3123"
    :type "A"
    },
   {:id 2
    :value 35.31
    :date "9.8.1229"
    :type "B"
    }
   ])

;; Return all users sites
(defn get-sites [user-id]
  (print "I should return users sites")
  {:status "Get WIP"
   :data mock-entries} 
   )
