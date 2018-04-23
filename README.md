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
