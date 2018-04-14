(ns plain.someactivity2
  (:require [clojure.tools.nrepl.server :as repl]
            [clojure.core.async :as a]
            [org.httpkit.client :as http])
  (:import
   (android.support.v7.app AppCompatActivity)
   (android.util Log)
   (com.example.ndksample.myapplication.R$id)
   (com.example.ndksample.myapplication.R$layout)
   (android.os Handler Bundle)
   (org.tensorflow.contrib.android TensorFlowInferenceInterface)
   (org.tensorflow Operation)
   (android.graphics Bitmap)
   (android.text.method ScrollingMovementMethod)
   (android.view View View$OnClickListener)
   (android.widget Button ImageView TextView)
   (com.wonderkiln.camerakit CameraKitError CameraKitEvent CameraKitEventListener CameraKitImage CameraKitVideo CameraView)
   (java.util List)
   (java.util.concurrent Executor Executors)
   (com.mindorks.tensorflowexample TensorFlowImageClassifier Classifier))
  (:gen-class
   :name "plain.someactivity2.MyActivity"
   :exposes-methods {onCreate superOnCreate}
   :extends android.support.v7.app.AppCompatActivity
   :prefix "some-"))

(defn fetch [url]
  (http/get url))

(defonce camera-view-atom (atom nil))
(defonce classifier-atom (atom nil))

(defn some-onResume [^plain.someactivity2.MyActivity this]
  (.superOnResume this bundle)
  (.start @camera-view-atom))

(defn some-onPause [^plain.someactivity2.MyActivity this]
  (.stop @camera-view-atom)
  (.superOnPause this bundle))

(defn some-onDestroy [^plain.someactivity2.MyActivity this]
  (.superOnDestroy this bundle)  
  ;;        executor.execute(new Runnable() {
  ;;            @Override
  ;;            public void run() {
  ;;                classifier.close();
  ;;            }
  ;;        });
  )

(defn some-onCreate [^plain.someactivity2.MyActivity this ^android.os.Bundle bundle]
  (.superOnCreate this bundle)
  (.setContentView this com.example.ndksample.myapplication.R$layout/activity_main)

  (try
    (do
      (Log/i "repl 启动中" "...")
      (repl/start-server :bind "127.0.0.1" :port 6868))
    (catch Exception e
      (Log/i "已启动" "clojure repl server")))
  
  (let [camera-view (.findViewById this com.example.ndksample.myapplication.R$id/cameraView)
        _ (reset! camera-view-atom camera-view)
        image-view-result (.findViewById this com.example.ndksample.myapplication.R$id/imageViewResult)
        text-view-result (.findViewById this com.example.ndksample.myapplication.R$id/textViewResult)
        _ (.setMovementMethod text-view-result (ScrollingMovementMethod.))
        btn-toggle-camera (.findViewById this com.example.ndksample.myapplication.R$id/btnToggleCamera)
        btn-detect-object (.findViewById this com.example.ndksample.myapplication.R$id/btnDetectObject)]
    (.addCameraKitListener camera-view
                           (proxy [CameraKitEventListener] []
                             (onEvent [^CameraKitEvent camera-kit-event]
                               (Log/i "onEvent" (str camera-kit-event)) )
                             (onError [^CameraKitError camera-kit-error]
                               (Log/i "onError" (str camera-kit-error)) )
                             (onImage [^CameraKitImage camera-kit-image]
                               (Log/i "onImage" "......")
                               (let [bitmap (.getBitmap camera-kit-image)
                                     bitmap (Bitmap/createScaledBitmap bitmap 224 224 false)
                                     _ (.setImageBitmap image-view-result bitmap)
                                     results (.recognizeImage @classifier-atom bitmap)]
                                 (.setText text-view-result (.toString results))) )
                             (onVideo [^CameraKitVideo camera-kit-video] )))
    ;;
    (.setOnClickListener btn-toggle-camera
                         (proxy [View$OnClickListener] []
                           (onClick [^View v]
                             (.toggleFacing camera-view))))
    ;;
    (.setOnClickListener btn-detect-object
                         (proxy [View$OnClickListener] []
                           (onClick [^View v]
                             (.captureImage camera-view))))
    )
  #_(.. this
      (findViewById com.example.ndksample.myapplication.R$id/getButton)
      (setOnClickListener (reify android.view.View$OnClickListener
                            (onClick [this v]
                              (Log/i "clojure" "hello")))))

  #_(let [tv (.findViewById this com.example.ndksample.myapplication.R$id/text)
        handler (Handler.)]
    (.start (Thread. (fn []
                       (let [data (:body @(fetch "https://clojure.org"))]
                         (.post handler #(.setText tv data))))))))
