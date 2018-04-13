(ns plain.someactivity2
  (:require [clojure.tools.nrepl.server :as repl]
            [clojure.core.async :as a]
            [org.httpkit.client :as http])
  (:import
   (android.support.v7.app AppCompatActivity)
   (android.util Log)
   (com.example.ndksample.myapplication.R$id)
   (com.example.ndksample.myapplication.R$layout)
   (android.os Handler)
   (org.tensorflow.contrib.android TensorFlowInferenceInterface)
   (org.tensorflow Operation))
  (:gen-class
   :name "plain.someactivity2.MyActivity"
   :exposes-methods {onCreate superOnCreate}
   :extends android.support.v7.app.AppCompatActivity
   :prefix "some-"))

(defn fetch [url]
  (http/get url))

(defn some-onCreate [^plain.someactivity2.MyActivity this ^android.os.Bundle bundle]
  (.superOnCreate this bundle)
  (.setContentView this com.example.ndksample.myapplication.R$layout/activity_main)

  (try
    (do
      (Log/i "repl 启动中" "...")
      (repl/start-server :bind "127.0.0.1" :port 6868))
    (catch Exception e
      (Log/i "已启动" "clojure repl server")))
  
  (.. this
      (findViewById com.example.ndksample.myapplication.R$id/getButton)
      (setOnClickListener (reify android.view.View$OnClickListener
                            (onClick [this v]
                              (Log/i "clojure" "hello")))))

  (let [tv (.findViewById this com.example.ndksample.myapplication.R$id/text)
        handler (Handler.)]
    (.start (Thread. (fn []
                       (let [data (:body @(fetch "https://clojure.org"))]
                         (.post handler #(.setText tv data))))))))
