(ns milkilo-tracker.layout
  (:require [selmer.parser :as parser]
            [clojure.string :as s]
            [ring.util.response :refer [content-type response]]
            [compojure.response :refer [Renderable]]
            [environ.core :refer [env]]))

(parser/set-resource-path! (clojure.java.io/resource "templates"))

(deftype
  RenderableTemplate
  [template params]
  Renderable
  (render
    [this request]
    (content-type
      (->>
        (assoc
          params
          :dev
          (env :dev)
          :servlet-context
          (if-let [context (:servlet-context request)]
            (try
              (.getContextPath context)
              (catch IllegalArgumentException _ context))))
        (parser/render-file (str template))
        response)
      "text/html; charset=utf-8")))

(defn render [template & [params]]
  (RenderableTemplate. template params))

