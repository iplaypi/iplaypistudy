![](https://img.shields.io/badge/language-Java-orange.svg) ![GitHub issues](https://img.shields.io/github/issues/iplaypi/iplaypistudy?color=blue) ![GitHub](https://img.shields.io/github/license/iplaypi/iplaypistudy?color=green)

# iplaypistudy
学习综合项目，存放示例代码、脚本。

# 初始化环境

## 1、下载代码，使用 IDEA 打开
执行：`git clone https://github.com/iplaypi/iplaypistudy.git`

## 2、根据自己的环境添加配置文件到 iplaypistudy-common-config 模块的 reaources 目录下
一般包含五个配置文件，分别是：

- `core-site.xml`
- `hbase-site.xml`
- `hdfs-site.xml`
- `mapred-site.xml`
- `yarn-site.xml`

## 3、根据功能需要编译执行
例如，只需要打包某一个模块：`mvn clean package -P dev -pl iplaypistudy-hbase -am`

# 自动化脚本
为了编译打包后方便执行某些功能代码，把环境设置过程、执行的命令整理为脚本：`run.sh`

## 通过脚本执行功能
编译打包后，进入 `target` 目录下，可以找到 `run.sh` 脚本，根据需要执行即可。
示例：`sh run.sh 功能标识 参数列表`
