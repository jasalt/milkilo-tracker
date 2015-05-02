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
  "Return entries for a site-id. Site-id and nil attributes are removed. Time is transformed from sql-style to simple date map. Sorted by date."
  ;; TODO separate function from-sql-form
  (->> (select entries
               (where {:site_id site-id}))
       (map #(dissoc % :site_id)) ;; Remove site ID
       (map (fn [entry] ;; Strip empty fields
              (into {}
                    (filter
                     (comp not nil? val) entry))))
       (sort-by :date)
       (reverse)
       ;; DB date into a simple date map
       (map #(let [date (c/from-sql-date (:date %))
                   date-map {:year (t/year date)
                             :month (t/month date)
                             :day (t/day date)}]
               (assoc % :date date-map)))
       ;; Transform (weird?) DB type-value into more human friendly form
       (map (fn [db-entry]
              (let [type-value-row (dissoc db-entry :id :date)
                    type (first (keys type-value-row))
                    value (first (vals type-value-row))]
                (-> db-entry
                    (assoc :value value :type type)
                    (dissoc type)))))))

(defn get-user-data [user-id]
  ;; Get data of all users administered sites with entries.
  (let [user-sites (get-administered-sites user-id)
        cleaned-user-sites (map #(select-keys % [:id :name]) user-sites)]
    (map
     (fn [site]
       (merge site {:entries (get-entries (site :id))}))
     cleaned-user-sites)))

(defn str->int [str]
  "Convert string to number if possible, or just return the string."
  (let [result (read-string str)]
    (if (number? result) result str)))


(defn to-sql-form [entry]
  (-> (assoc
       (select-keys entry [:site_id])
       ;; Convert type value keys into DB row value
       ;; TODO hack fix to convert strings numbers
       (entry :type) (str->int (:value entry)))

      ;; Convert date map to sql-time
      (assoc  :date
              (c/to-sql-time
               (t/date-time ((entry :date) :year)
                            ((entry :date) :month)
                            ((entry :date) :day))))))

(defn insert-entry [entry]
  (insert entries (values (to-sql-form entry)))
  ;; TODO convert actual db response converted into app-readable format
  entry
  )

(defn update-entry [entry]
  (update entries
          (set-fields (to-sql-form entry))
          (where {:id (entry :id)}))
  entry
  )
(defn delete-entry [entry]
  (delete entries
          (where {:id (entry :id)}))
  entry
  )
