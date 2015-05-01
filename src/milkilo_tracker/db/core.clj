(ns milkilo-tracker.db.core
  (:use korma.core
        [korma.db :only (defdb)]
        clojure.pprint)
  (:require
   [milkilo-tracker.db.schema :as schema]
   [clj-time.core :as t]
   [clj-time.coerce :as c]
   ))

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
  "Return entries for a site-id. Site-id and nil attributes are removed. Time is transformed from sql-style to simple date map."
  (let [db-entries (select entries
                           (where {:site_id site-id}))
        without-site-id (map #(dissoc % :site_id) db-entries)
        without-empty-fields (map (fn [entry]
                                    (into {}
                                          (filter
                                           (comp not nil? val) entry)))
                                  without-site-id)
        with-simple-date
        (map #(let [date (c/from-sql-date (:date %))
                    date-map {:year (t/year date)
                              :month (t/month date)
                              :day (t/day date)}]
                (assoc % :date date-map)) without-empty-fields)]
    with-simple-date))

(defn get-user-data [user-id]
  ;; Get data of all users administered sites with entries.
  (let [user-sites (get-administered-sites user-id)
        cleaned-user-sites (map #(select-keys % [:id :name]) user-sites)]
    (map
     (fn [site]
       (merge site {:entries (get-entries (site :id))}))
     cleaned-user-sites)))

;; TODO to-sql-time here
(defn insert-entry [entry]
  (insert entries
          (values entry)))

(defn update-entry [entry]
  nil
  )
