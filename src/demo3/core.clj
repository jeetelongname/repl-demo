(ns demo3.core
  (:require
   [ring.adapter.jetty :as j]
   [clojure.pprint :as pp]
   [hiccup.core :as h]
   [clojure.string :as str]))

(defn document
  [& body]
  (h/html
   [:html
    [:head
     [:link {:rel "stylesheet" :href "https://unpkg.com/@picocss/pico@1.*/css/pico.min.css"}]]
    [:body.container body]]))

(defn hello-handler [_]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body #_{:clj-kondo/ignore [:deprecated-var]}
   (h/html (document [:h1 [:bold "Hello everyone"]]))})

(defn sum-handler
  [request]
  (let [number (if (:query-string request)
                 (-> request
                     :query-string
                     (str/split #"=")
                     last
                     parse-long)
                 nil)

        number-str (loop [n number sum 0]
                     (cond
                       (nil? n) "No number provided"
                       (= number 13) "Error"
                       (= 0 n)  (str sum)
                       :else (recur (dec n) (+ sum n))))

        ;; number-str (if (nil? number)
        ;;              "No number provided"
        ;;              (/ (* number (inc number)) 2))
        ]

    {:status 200
     :headers {"Content-Type" "text/html"}
     :body (h/html (document
                    [:h1 "Sum some numbers"]
                    [:form {:method "get" :action "/sum"}
                     [:label {:for "number"} "Enter Number"]
                     [:input#number {:type "text" :name "number"}]]
                    [:p "Sum: " number-str]))}))

(def router {"/hello" #'hello-handler
             "/sum"   #'sum-handler})

(defn default [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (h/html
          (document
           [:h1 "Hello from " [:em (:uri request)]]
           [:p "somehting"]))})

(defn app
  [request]
  (pp/pprint request)
  (let [handler (get router (:uri request) #'default)]
    (handler request)))

(def start #(j/run-jetty #'app {:port 3000 :join? false}))

(defn -main []
  (start))

(comment
  (println (+ 1 1)))
