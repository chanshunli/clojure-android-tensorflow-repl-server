# Android Tensorflow on Clojure repl sever

### Usage

* apk 下载安装 [app-debug.apk](https://raw.githubusercontent.com/chanshunli/clojure-android-tensorflow-repl-server/master/app-debug.apk)
* Emacs ` M-x cider-connect 6868 `

### Future
* 手机上`Termux + Emacs + Cider`, 用Clojure开发安卓应用
* 手机上开发测试使用Tensorflow的训练数据
* 方便Clojure学习者,随时随地手机上Repl学习一些库的使用, 如: [clojure async](https://github.com/clojure/core.async)等
* 手机上的数据分析, 和数据可视化中心(目前需要手机root或者把termux的命令环境权限授权给本APP才可以支持): 整合Termux命令环境的R, Python, Julia等
* 支持语音输入编程 (TODO)
* 整合clojupyter: 可视化部分支持incanter

### Android上较难解决的安装包问题: Python, R安装,gcc等Linux问题
* https://github.com/chanshunli/gcc_termux

### 开发环境效果

![](https://raw.githubusercontent.com/chanshunli/clojure-android-tensorflow-repl-server/master/demo.jpeg)
