(ns milkilo-tracker.middleware
  (:require [taoensso.timbre :as timbre]
            [selmer.parser :as parser]
            [environ.core :refer [env]]
            [selmer.middleware :refer [wrap-error-page]]
            [prone.middleware :refer [wrap-exceptions]]
            [noir-exception.core :refer [wrap-internal-error]]
            ;; For user authentication
            [noir.session :as session]
            [ring.middleware.session.memory :refer [memory-store]]
            [cemerick.friend :as friend]
            (cemerick.friend [workflows :as workflows]
                             [credentials :as creds])
            [milkilo-tracker.db.core :as db]
            ))

;; A dummy in-memory user "database" TODO
(def users {"root" {:username "root"
                    :password (creds/hash-bcrypt "password")
                    :roles #{::admin}}
            "user1" {:username "user1"
                     :password (creds/hash-bcrypt "password")
                     :roles #{::user}}})

;; TODO remove debugging print statements
(defn credential-fn [auth-map]
  (let [email (auth-map :username)
        password (auth-map :password)
        db-user (db/get-user {:email email})]

    (println (str "Got auth-map " auth-map))
    (if db-user
      (let [hash-stored (db-user :password)]
        (println "Found user by email, comparing given password to stored hash")
        (if (creds/bcrypt-verify password hash-stored)
          (do
            (println "Passwords match!")
            db-user
            )
          (println "Passwords don't match"))
        )
      (println "Email address not found"))
    )
  )

(defn log-request [handler]
  (fn [req]
    (timbre/debug req)
    (handler req)))

(def development-middleware
  [wrap-error-page
   wrap-exceptions])

(def production-middleware
  [#(wrap-internal-error % :log (fn [e] (timbre/error e)))
   #(session/wrap-noir-session % {:store (memory-store)})
   #(friend/authenticate % {:credential-fn credential-fn
                            :workflows [(workflows/interactive-form)]})
   ])

(defn load-middleware []
  (concat (when (env :dev) development-middleware)
          production-middleware))
