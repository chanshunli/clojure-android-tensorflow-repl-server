# Android Tensorflow on Clojure repl sever

### 随时随地手机开发,安卓上的Clojure数据分析开发环境,整合clojupyter 

### Usage

* apk 下载安装 [app-debug.apk](https://raw.githubusercontent.com/chanshunli/clojure-android-tensorflow-repl-server/master/app-debug.apk)
* Emacs ` M-x cider-connect 6868 `

### Future
* 手机上`Termux + Emacs + Cider`, 用Clojure开发安卓应用
* 手机上开发测试使用Tensorflow的训练数据
* 方便Clojure学习者,随时随地手机上Repl学习一些库的使用, 如: [clojure async](https://github.com/clojure/core.async)等
* 手机上的数据分析, 和数据可视化中心(目前需要手机root或者把termux的命令环境权限授权给本APP才可以支持): 整合Termux命令环境的R, Python, Julia等
* 支持语音输入编程 (TODO: 手机上不便于复杂符号和快捷键ctrl-meta-option的输入,但是手机触屏环境很适合多点输入=>可以做成树形化的输入环境,同步转译为代码,借鉴Tableau的思想,通过UI的交互历史来编译生成S表达式代码和SQL等)
* 整合clojupyter: 可视化部分支持incanter(目前incanter无法在安卓上编译通过)

### Android上较难解决的安装包问题: Python, R安装,gcc等Linux问题
* https://github.com/chanshunli/gcc_termux

### 开发环境效果

![](https://raw.githubusercontent.com/chanshunli/clojure-android-tensorflow-repl-server/master/demo.jpeg)

### 和安卓Java代码互操作

```clojure
(ns plain.someactivity2
  (:require
   [clojure.tools.nrepl.server :as repl]
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
   :exposes-methods {onCreate superOnCreate
                     onResume superOnResume
                     onPause superOnPause
                     onDestroy superOnDestroy}
   :extends android.support.v7.app.AppCompatActivity
   :prefix "some-"))

(defn fetch [url]
  (http/get url))

(defonce camera-view-atom (atom nil))
(defonce classifier-atom (atom nil))
(def executor (Executors/newSingleThreadExecutor))

(defn some-onResume [^plain.someactivity2.MyActivity this]
  (.superOnResume this)
  (.start @camera-view-atom))

(defn some-onPause [^plain.someactivity2.MyActivity this]
  (.stop @camera-view-atom)
  (.superOnPause this))

(defn some-onDestroy [^plain.someactivity2.MyActivity this]
  (.superOnDestroy this))

(defonce this-atom (atom nil))
(defonce btn-detect-object-atom (atom nil))

(defn some-onCreate [^plain.someactivity2.MyActivity this ^android.os.Bundle bundle]
  (.superOnCreate this bundle)
  (.setContentView this com.example.ndksample.myapplication.R$layout/activity_main)
  (reset! this-atom this)
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
        btn-detect-object (.findViewById this com.example.ndksample.myapplication.R$id/btnDetectObject)
        _ (reset! btn-detect-object-atom btn-detect-object)]
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
    ;;
    (do
      (reset!
       classifier-atom
       (TensorFlowImageClassifier/create
        (.getAssets this)
        "file:///android_asset/tensorflow_inception_graph.pb"
        "file:///android_asset/imagenet_comp_graph_label_strings.txt"
        224 117 1 "input" "output"))
      (.runOnUiThread this
                      (proxy [Runnable] []
                        (run []
                          (.setVisibility btn-detect-object View/VISIBLE))))
      (Log/i "初始化TensorFlow成功!" "..."))
    )
  )

```
