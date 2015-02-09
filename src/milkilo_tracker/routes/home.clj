(ns milkilo-tracker.routes.home
  (:require [milkilo-tracker.layout :as layout]
            [milkilo-tracker.util :as util]
            [compojure.core :refer :all]
            [noir.response :refer [edn, redirect]]
            [cemerick.friend :as friend]
            [clojure.pprint :refer [pprint]]))

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

;; TODO Hackfixed authentication exception handling
;; Why friends default unauth-handler does not catch error and redirect?
(defn home-page []
  (try
    (friend/authenticated (layout/render "app.html"))
  (catch Exception e
    (do
      (println "GOT IT ++++++++++++++++")
      (println (str "caught exception: " (.getMessage e)))
      (redirect "/login")
      )
    )
  )
)

(defn get-entries []
  (pprint "Return mock entries")
  {:status "Get WIP"
   :data mock-entries}
  )

(defn save-entry [doc]
  (pprint doc)
  {:status "Post successful"})

(defn save-document [doc]
  (pprint doc)
  {:status "ok"})

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/entries" [] (edn (get-entries)))
  (POST "/entry" {:keys [body-params]} (edn (save-entry body-params)))
  )
