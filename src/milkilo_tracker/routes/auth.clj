(ns milkilo-tracker.routes.auth
  (:require [milkilo-tracker.layout :as layout]
            [compojure.core :refer :all]
            [cemerick.friend :as friend]
            ))

(defn login-page []
  (layout/render
   "login.html"))

(defroutes auth-routes
  (GET "/login" [] (login-page))
  (GET "/secret" [] (friend/authorize #{:milkilo-tracker.middleware/user} "Logged in, it seems."))
  (friend/logout (ANY "/logout" request (ring.util.response/redirect "/")))
  )
