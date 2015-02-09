(ns milkilo-tracker.routes.auth
  (:require [milkilo-tracker.layout :as layout]
            [milkilo-tracker.util :as util]
            [compojure.core :refer :all]
            [noir.response :refer [edn]]
            [clojure.pprint :refer [pprint]]
            [cemerick.friend :as friend]
            ))

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

(defn login-page []
  (layout/render
   "login.html"))

(defn login-page []
  (layout/render
   "login.html"))

(defroutes auth-routes
  (GET "/login" [] (login-page))
  (GET "/secret" [] (friend/authorize #{::user} "Logged in, it seems."))
  )
