(ns demo3.core
  (:require
   [reitit.ring :as ring]
   [ring.adapter.jetty :as j]
   [clojure.pprint :as pp]
   [hiccup.core :as h]))

(defn hello-handler [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "Hello Joe\n"})

(defn sum-handler
  ""
  [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (h/html [:p "hello world"])})

(def router {"/hello" hello-handler
             "/sum"   sum-handler})

(defn default [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (h/html [:p "Hello from " [:strong (:uri request)]])})

(defn app
  [request]
  (pp/pprint request)
  (let [handler (get router (:uri request) default)]
    (handler request)))

(comment
  (defonce server (j/run-jetty #'app {:port 3000 :join? false}))
  (.stop server))
