# telecom-bigdata
本项目以电信数据可视化为最终目的实现了一个完整的大数据工作流，可分为以下几个步骤：数据生产，数据采集，数据存储，数据分析，数据聚类，数据可视化。

## 功能模块说明

| 模块                   | 说明                                                         |
| ---------------------- | ------------------------------------------------------------ |
| telecom-data-generator | Java电信数据生成器                                           |
| kafka-producer         | Kafka生产者，从CSV文件采集数据                               |
| kafka-consumer         | Kafka消费者，把数据保存到Hive数据库                          |
| data-analysis          | Spark数据分析程序，结果保存在MySQL                           |
| spark-ml               | Spark ml用户聚类程序，结果保存在MySQL                        |
| dashboard              | 基于[Toolpad Core](https://mui.com/toolpad/core/introduction)构建的可视化网页 |
| spring                 | Spring Boot（Spring Security，Spring Data JPA）后端          |
| cluster_visualization  | python脚本，验证和评估用户聚类结果                           |
| database-sql           | 用到的SQL和HQL                                               |

## 仿真环境

### 系统环境

大数据集群使用VMware WorkStation搭建，系统镜像Ubuntu 24.04.3 desktop，4个节点（master，slave1，slave2，slave3）。

| 节点   | 硬盘 | 内存                     | CPU  |
| ------ | ---- | ------------------------ | ---- |
| master | 60G  | 4.5G（和相同容量的swap） | 2核  |
| slave  | 40G  | 4G（和相同容量的swap）   | 2核  |

### 软件框架

集群中用到的软件或框架：

| 软件   | 版本                    |
| ------ | ----------------------- |
| Hadoop | 3.4.1                   |
| Hive   | 3.1.3                   |
| Spark  | 3.5.4 (with Scala 2.13) |
| Kafka  | 3.8.1 (with Scala 2.13) |

### 编程语言

| 语言   | 版本                                                         |
| ------ | ------------------------------------------------------------ |
| Java   | 8（虚拟机上，用于大数据部分）和21（主机上，用于Spring Boot） |
| Scala  | 2.13（加载需要用到的项目时Maven会自动安装）                  |
| Python | 3.12                                                         |

## 环境搭建

具体的搭建步骤可以参考[ENV_SETUP](ENV_SETUP.md)。由于这份流程是写给自己看的，行文很随意，也可能有一些遗漏和错误的地方，请谅解。

搭建的过程中经常需要在多个节点之间同步文件，所以我写了个小工具[xsync](xsync)。最常见的用法是，在`/usr/xxx/yyy`目录下时，直接运行`xsync`，就会把4个节点`/usr/xxx/yyy`目录下的内容同步成一样的。

## 常用命令

用于快速地过一遍流程，见[COMMON_CMD](COMMON_CMD.sh)。
