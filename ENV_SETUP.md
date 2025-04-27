# 配置流程

## 创建虚拟机

master（内存4.5GB，硬盘60GB），3个slave（内存4GB，硬盘40GB）

## 安装系统master（Ubuntu 24.04.3 desktop）

进入系统更新软件包列表

```shell
sudo apt update
```

## 更新系统软件（设置 - 系统 - 检查更新）

## 安装vmtools，再重启

```shell
sudo apt install open-vm-tools open-vm-tools-desktop -y
```

## 配置root密码：

```shell
sudo passwd root
```

## 允许root用户登录GUI：

```shell
sudo gnome-text-editor /etc/pam.d/gdm-password
开头处带有auth required pam_succeed_If的行注释掉
```

## 注销，以root身份登录GUI

如果不使用root，则在master和三个slave上都使用一个相同的用户名，比如hadoop，系统安装的时候就可以填好。

注意在其他地方（命令和脚本中），用户名要同步修改。

## 系统设置-电源-息屏（从不）

## 修改 /etc/hostname 和 /etc/hosts

```sh
gnome-text-editor /etc/hostname /etc/hosts
```

```
# 注意：域名（比如master）不能和127.0.0.1本地环回地址绑定！
192.168.40.200 master
192.168.40.201 slave1
192.168.40.202 slave2
192.168.40.203 slave3
```

## 设置ip

在系统设置-网络-有线里配置每台机器的ip，子网掩码，网关，DNS

如果主机能ping通虚拟机，虚拟机Ping不通主机，看一下主机的windows defencer入站规则 ICMP v4

## 安装ssh

```shell
sudo apt install openssh-server -y
sudo systemctl enable ssh
sudo ufw allow ssh
```

## 配置ssh

```shell
sudo gnome-text-editor /etc/ssh/sshd_config
PermitRootLogin prohibit-password
# 改为：
PermitRootLogin yes

sudo systemctl restart ssh
```

## 在windows配置SSH和SFTP连接

## 安装JAVA：

```shell
sudo apt install openjdk-11-jdk -y
```

需要安装JAVA 8（11有兼容问题）。卸载JAVA 11时，除了apt remove openjdk-11-jdk，还需要apt autoremove。

## 通过SFTP把hadoop-3.4.1.tar.gz传到master

SFTP文件中文乱码解决：Global options - configuration paths，打开这个路径，打开Sessions，修改配置文件中的`Filenames Always Use UTF8`，末尾改成1。

## 解压并移动

```shell
tar -xzvf hadoop-3.4.1.tar.gz
sudo mv hadoop-3.4.1 /usr/local/hadoop
```

## 创建文件xsync

## 给执行权限

```shell
sudo chmod +x xsync
```

## 放到sbin

```shell
mv xsync /usr/local/sbin/
```

## 生成SSH密钥（每个node）

```shell
ssh-keygen -t rsa -P ""
```

## 复制ssh密钥（master上操作，注意当前用户）

```shell
ssh-copy-id master
ssh-copy-id slave1
ssh-copy-id slave2
ssh-copy-id slave3

```

## 加环境变量

```shell
sudo gnome-text-editor ~/.bashrc
或者
sudo gnome-text-editor /etc/profile
# 加入以下内容（哪些有用待探究）可以在hadoop配置文件的那些env文件中找到变量名
# 每个节点都要配置，所以需要同步

# Hadoop
export HADOOP_HOME=/usr/local/hadoop
export HIVE_HOME=/usr/local/hive
export SPARK_HOME=/usr/local/spark
export HADOOP_CONF_DIR=$HADOOP_HOME/etc/hadoop
export PATH=$PATH:$HADOOP_HOME/bin:$HADOOP_HOME/sbin:$HIVE_HOME/bin
export HADOOP_COMMON_HOME=$HADOOP_HOME
export HADOOP_HDFS_HOME=$HADOOP_HOME
export HADOOP_YARN_HOME=$HADOOP_HOME
export HADOOP_MAPRED_HOME=$HADOOP_HOME
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
export HADOOP_USER_NAME=root
export HDFS_DATANODE_USER=root
export HDFS_NAMENODE_USER=root
export HDFS_SECONDARYNAMENODE_USER=root
export YARN_RESOURCEMANAGER_USER=root
export YARN_NODEMANAGER_USER=root
export HADOOP_HEAPSIZE=1024

export CLASSPATH=$CLASSPATH:/usr/local/hadoop/lib/native:/usr/local/hive/lib
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/usr/local/hadoop/lib/native:/usr/local/hive/lib
```

## Hadoop配置文件

/usr/local/hadoop/etc/hadoop

```sh
gnome-text-editor hadoop-env.sh core-site.xml hdfs-site.xml mapred-site.xml yarn-site.xml workers
```

### hadoop-env.sh

```sh
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
```

### core-site.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>

<configuration>
    <property>
        <name>fs.defaultFS</name>
        <value>hdfs://master:9000</value>
    </property>
    <property>
        <name>hadoop.http.staticuser.user</name>
        <value>root</value>
    </property>
    <property>
        <name>hadoop.proxyuser.root.hosts</name>
        <value>*</value>
    </property>
    <property>
        <name>hadoop.proxyuser.root.groups</name>
        <value>*</value>
    </property>
    <property>
        <name>hadoop.tmp.dir</name>
        <value>/usr/local/hadoop/tmp</value>
    </property>
    <property>
        <name>io.file.buffer.size</name>
        <value>131072</value>
    </property>
</configuration>
```



### hdfs-site.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
<configuration>
    <property>
        <name>dfs.replication</name>
        <value>3</value>
    </property>
    <property>
        <name>dfs.namenode.name.dir</name>
        <value>file:/usr/local/hadoop/hdfs/namenode</value>
    </property>
    <property>
        <name>dfs.datanode.data.dir</name>
        <value>file:/usr/local/hadoop/hdfs/datanode</value>
    </property>
    <property>
        <name>dfs.namenode.http-address</name>
        <value>0.0.0.0:9870</value>
    </property>
</configuration>
```

### mapred-site.xml

```xml
<?xml version="1.0"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>

<configuration>
  <property>
    <name>mapreduce.framework.name</name>
    <value>yarn</value>
  </property>
</configuration>
```

### yarn-site.xml

```xml
<?xml version="1.0"?>
<configuration>

    <!-- Site specific YARN configuration properties -->
    <property>
        <name>yarn.resourcemanager.hostname</name>
        <value>master</value> 
    </property>
    <property>
        <name>yarn.nodemanager.aux-services</name>
        <value>mapreduce_shuffle</value>
    </property>
    <property>
        <name>yarn.resourcemanager.webapp.address</name>
        <value>0.0.0.0:8088</value>
    </property>
    <property>
        <name>yarn.application.classpath</name>
        <value>/usr/local/hadoop/etc/hadoop:/usr/local/hadoop/share/hadoop/common/lib/*:/usr/local/hadoop/share/hadoop/common/*:/usr/local/hadoop/share/hadoop/hdfs:/usr/local/hadoop/share/hadoop/hdfs/lib/*:/usr/local/hadoop/share/hadoop/hdfs/*:/usr/local/hadoop/share/hadoop/mapreduce/*:/usr/local/hadoop/share/hadoop/yarn:/usr/local/hadoop/share/hadoop/yarn/lib/*:/usr/local/hadoop/share/hadoop/yarn/*</value> 
    </property>
</configuration>
```

### workers

```
slave1
slave2
slave3
```

## 使用xsync分发hadoop文件

```sh
# hadoop顶层目录内
xsync
```

## 格式化HDFS（master）

```
hdfs namenode -format
```

### 启动Hadoop命令

```
start-dfs.sh
start-yarn.sh
```

### 测试

```sh
hdfs dfsadmin -report

# 主机浏览器打开：
# namenode
http://master:9870
# yarn
http://master:8088
```

## 通过SFTP把apache-hive-3.1.3-bin.tar.gz传到master

## 解压并移动

```shell
tar -xzvf apache-hive-3.1.3-bin.tar.gz
sudo mv apache-hive-3.1.3-bin /usr/local/hive
```

## Hive配置文件

切换到Hive目录的conf文件夹内

```shell
mv hive-env.sh.template hive-env.sh
```

感觉应该不用改env，跳过

```sh
mv hive-log4j2.properties.template hive-log4j2.properties
```

log4j里有一项`property.hive.log.dir`可以考虑改（日志位置）。

```sh
mv hive-default.xml.template hive-site.xml
```

hive-site.xml要修改的：

```xml
<property>
    <name>hive.repl.rootdir</name>
    <value>/user/hive/repl/</value>
    <description>HDFS root dir for all replication dumps.</description>
</property>
<property>
    <name>hive.repl.cmrootdir</name>
    <value>/user/hive/cmroot/</value>
    <description>Root dir for ChangeManager, used for deleted files.</description>
</property>
<property>
    <name>hive.exec.local.scratchdir</name>
    <value>/usr/local/hive/scratchdir</value>
    <description>Local scratch space for Hive jobs</description>
</property>
<property>
    <name>hive.downloaded.resources.dir</name>
    <value>/usr/local/hive/resources/${hive.session.id}</value>
    <description>Temporary local directory for added resources in the remote file system.</description>
</property>
<property>
    <name>hive.metastore.uris</name>
    <value>thrift://master:9083</value>
    <description>Thrift URI for the remote metastore. Used by metastore client to connect to remote metastore.</description>
</property>
<property>
    <name>javax.jdo.option.ConnectionPassword</name>
    <value>root</value>
    <description>password to use against metastore database</description>
</property>
<property>
    <name>javax.jdo.option.ConnectionURL</name>
    <value>jdbc:mysql://master:3306/hive_metastore?createDatabaseIfNotExist=true</value>
    <description>
        JDBC connect string for a JDBC metastore.
        To use SSL to encrypt/authenticate the connection, provide database-specific SSL flag in the connection URL.
        For example, jdbc:postgresql://myhost/db?ssl=true for postgres database.
    </description>
</property>
<property>
    <name>hive.metastore.event.db.notification.api.auth</name>
    <value>false</value>
    <description>
        Should metastore do authorization against database notification related APIs such as get_next_notification.
        If set to true, then only the superusers in proxy settings have the permission
    </description>
</property>
<property>
    <name>javax.jdo.option.ConnectionDriverName</name>
    <value>com.mysql.cj.jdbc.Driver</value>
    <description>Driver class name for a JDBC metastore</description>
</property>
<property>
    <name>javax.jdo.option.ConnectionUserName</name>
    <value>root</value>
    <description>Username to use against metastore database</description>
</property>
<property>
    <name>hive.querylog.location</name>
    <value>/usr/local/hive/querylog</value>
    <description>Location of Hive run time structured log file</description>
</property>
<property>
    <name>hive.server2.thrift.bind.host</name>
    <value>master</value>
    <description>Bind host on which to run the HiveServer2 Thrift service.</description>
</property>
<property>
    <name>hive.server2.logging.operation.log.location</name>
    <value>/usr/local/hive/operation_logs</value>
    <description>Top level directory where operation logs are stored if logging functionality is enabled</description>
</property>
<property>
    <name>hive.cli.print.current.db</name>
    <value>true</value>
    <description>Whether to include the current database in the Hive prompt.</description>
</property>
<property>
    <name>hive.server2.thrift.client.user</name>
    <value>root</value>
    <description>Username to use against thrift client</description>
</property>
<property>
    <name>hive.server2.thrift.client.password</name>
    <value>root</value>
    <description>Password to use against thrift client</description>
</property>
<property>
    <name>hive.users.in.admin.role</name>
    <value>root</value>
    <description>
        Comma separated list of users who are in admin role for bootstrapping.
        More users can be added in ADMIN role later.
    </description>
</property>
<property>
    <name>hive.security.authorization.enabled</name>
    <value>true</value>
    <description>enable or disable the Hive client authorization</description>
</property>
```

## 安装mysql

```shell
sudo apt install mysql-server -y
```

### 运行mysql安装脚本

```shell
sudo mysql_secure_installation
```

### 设置hive用户和密码（也可以直接用root，跳过这步即可）

```sh
sudo mysql -u root -p
```

```sql
CREATE DATABASE hive_metastore;
CREATE USER 'hiveuser'@'%' IDENTIFIED BY 'hivepassword';
GRANT ALL PRIVILEGES ON hive_metastore.* TO 'hiveuser'@'%';
FLUSH PRIVILEGES;
EXIT;
```

### 通过SFTP把mysql-connector-j-9.1.0.tar.gz传到master

### 解压并移动到hive的lib目录

```shell
tar -xzvf mysql-connector-j-9.1.0.tar.gz
sudo cp mysql-connector-j-9.1.0/mysql-connector-j-9.1.0.jar $HIVE_HOME/lib/
```

### 修改配置文件，允许远程登录

打开`/etc/mysql/mysql.conf.d/mysqld.cnf`，修改内容`bind-address		= 0.0.0.0`

### 允许root用密码远程登录

```sql
# 宽松的密码
SHOW VARIABLES LIKE 'validate_password%';

SET GLOBAL validate_password.check_user_name = 'OFF';
SET GLOBAL validate_password.length = 0;
SET GLOBAL validate_password.mixed_case_count = 0;
SET GLOBAL validate_password.number_count = 0;
SET GLOBAL validate_password.policy = 'LOW';
SET GLOBAL validate_password.special_char_count = 0;

ALTER USER 'root'@'localhost' IDENTIFIED WITH caching_sha2_password BY 'root';
UPDATE mysql.user SET Host='%' WHERE User='root' AND Host='localhost';
FLUSH PRIVILEGES;
```

## 格式化Hive在mysql的metastore数据库

格式化之前需上传Mysql的jar包到hive的lib

```shell
schematool -initSchema -dbType mysql
```

## 在HDFS中创建hive需要的目录并给权限

对应hive-site.xml配置项为`hive.metastore.warehouse.dir`，默认值为`/user/hive/warehouse`。

```sh
hdfs dfs -mkdir -p /user/hive/warehouse
hdfs dfs -chmod g+w /user/hive/warehouse
# 这个权限可能不行，因为hiveuser不一定在这个组里
# /tmp目录可能也需要给权限
# 如果是root应该问题不大？
hdfs dfs -chmod -R 777 /temp
```

## 使用xsync分发hive文件到slaves

hive只在master上运行，同步是因为spark要使用hive的jar文件。

## Hive测试

```sh
# 先启动dfs和yarn
hive --service metastore
hive --service hiveserver2
# 网络查看
netstat -nplt
# 应该得有10000端口的hiveserver2和10002的web ui
# 连接hive
beeline -u jdbc:hive2://master:10000 -n root -p root
# 或者先hive命令，然后connect
!connect jdbc:hive2://master:10000 root root
```

```sql
# 建数据库，建表，插入数据，查询数据测试
SHOW DATABASES;
CREATE DATABASE test_db;
USE test_db;
CREATE TABLE test_table (id INT, name STRING) ROW FORMAT DELIMITED FIELDS TERMINATED BY ',' STORED AS TEXTFILE;
SHOW TABLES;
INSERT INTO test_table VALUES (1, "aaa");
SELECT * FROM test_table;
# 进行需要Admin的操作前
set role ADMIN;
```

如果都通过说明Hadoop，HDFS，Mapreduce和Hive的配置应该都正确。

## Kafka（使用KRaft）安装

### 把kafka_2.13-3.9.0.tgz使用SFTP上传到master

但是master不是集群中的

### 解压并移动

```sh
tar -xzf kafka_2.13-3.9.0.tgz
sudo mv kafka_2.13-3.9.0 /usr/local/kafka
```

### 编辑配置文件

```
/usr/local/kafka/config/kraft/server.properties
```

关键点：

```sh
############################# Server Basics #############################

# The role of this server. Setting this puts us in KRaft mode
process.roles=broker,controller

# The node id associated with this instance's roles
node.id=1 # 每个节点不一样

# The connect string for the controller quorum
controller.quorum.voters=1@slave1:9093,2@slave2:9093,3@slave3:9093

# 。。。
# 每个节点ip不一样
listeners=PLAINTEXT://slave1:9092,CONTROLLER://slave1:9093

# Name of listener used for communication between brokers.
inter.broker.listener.name=PLAINTEXT

# Listener name, hostname and port the broker or the controller will advertise to clients.
# If not set, it uses the value for "listeners".
# 每个节点ip不一样, CONTROLLER部分可能需要删掉
# advertised.listeners=PLAINTEXT://slave1:9092,CONTROLLER://slave1:9093

# 。。。

# A comma separated list of directories under which to store log files
log.dirs=/usr/local/kafka/kraft-combined-logs

# The default number of log partitions per topic. More partitions allow greater
# parallelism for consumption, but this will also result in more files across
# the brokers.
num.partitions=3

# 。。。

# 定时清理日志（48小时过期）
# The minimum age of a log file to be eligible for deletion due to age
log.retention.hours=48

# 超过2GB清理
# A size-based retention policy for logs. Segments are pruned from the log unless the remaining
# segments drop below log.retention.bytes. Functions independently of log.retention.hours.
#log.retention.bytes=2147483648
```

**/usr/local/kafka/config**

```sh
# List of comma-separated URIs the REST API will listen on. The supported protocols are HTTP and HTTPS.
# Specify hostname as 0.0.0.0 to bind to all interfaces.
# Leave hostname empty to bind to default interface.
# Examples of legal listener lists: HTTP://myhost:8083,HTTPS://myhost:8084"
# 每个节点ip不一样
listeners=HTTP://slave1:8083

# The Hostname & Port that will be given out to other workers to connect to i.e. URLs that are routable from other servers.
# If not set, it uses the value for "listeners" if configured.
# 每个节点不一样
#rest.advertised.host.name=slave1
#rest.advertised.port=8083
#rest.advertised.listener=HTTP

# Set to a list of filesystem paths separated by commas (,) to enable class loading isolation for plugins
# (connectors, converters, transformations). The list should consist of top level directories that include 
# any combination of: 
# a) directories immediately containing jars with plugins and their dependencies
# b) uber-jars with plugins and their dependencies
# c) directories immediately containing the package directory structure of classes of plugins and their dependencies
# Examples: 
# plugin.path=/usr/local/share/java,/usr/local/share/kafka/plugins,/opt/connectors,
plugin.path=/usr/local/kafka/plugins
```

### connector安装

注：以下connect部分可跳过，没用到

SFTP上传并解压：

```sh
unzip streamthoughts-kafka-connect-file-pulse-2.14.1.zip
```

移动到插件目录：

```sh
mkdir /usr/local/kafka/plugins
mv streamthoughts-kafka-connect-file-pulse-2.14.1 /usr/local/kafka/plugins
```

因为是集群使用kafka connect，配置通过rest api操作，不用改配置文件。

### 使用xsync分发kafka文件到三个slave

然后修改配置文件

### 日志目录格式化

```sh
# slave1上生成ID
echo "$(bin/kafka-storage.sh random-uuid)"
# 在每个节点上格式化，使用相同的KAFKA_CLUSTER_ID
./bin/kafka-storage.sh format -t "7KLXZr8lR5-64GOtbE8Xjw" -c config/kraft/server.properties
```

### 启动（三个slave）

```sh
cd /usr/local/kafka
bin/kafka-server-start.sh config/kraft/server.properties
```

### 测试

```sh
# 查看基本信息
./bin/kafka-metadata-quorum.sh --bootstrap-controller slave1:9093 describe --status
# 创建topic
bin/kafka-topics.sh --create --topic telecom-data --bootstrap-server slave1:9092
# 生产数据
bin/kafka-console-producer.sh --topic telecom-data --bootstrap-server slave1:9092
# 消费
bin/kafka-console-consumer.sh --topic telecom-data --from-beginning --bootstrap-server slave1:9092
# 查看topic
bin/kafka-topics.sh --describe --topic telecom-data --bootstrap-server slave1:9092
# 删除topic
bin/kafka-topics.sh --delete --topic telecom-data --bootstrap-server slave1:9092
```

### 启动kafka connect

```sh
bin/connect-distributed.sh config/connect-distributed.properties
```

### connector

```sh
curl -sX GET http://slave1:8083/connector-plugins

curl -sX PUT http://slave1:8083/connectors/connect-file-pulse-quickstart-csv/config \
-d @/root/playground/connect-file-pulse-quickstart-csv.json \
--header "Content-Type: application/json" | jq

curl -X GET http://slave1:8083/connectors/connect-file-pulse-quickstart-csv/status | jq

curl -X DELETE http://slave1:8083/connectors/connect-file-pulse-quickstart-csv | jq
```



## Spark安装

需要改为使用Scala 2.13版本的Spark，kafka也用的2.13，这样能统一版本

### 把spark-3.5.4-bin-hadoop3.tgz通过SFTP发到master

### 解压并移动

```sh
tar -xvzf spark-3.5.4-bin-hadoop3.tgz
sudo mv spark-3.5.4-bin-hadoop3 /usr/local/spark
```

### 配置文件编辑

切换到/usr/local/spark/conf

```sh
mv log4j2.properties.template log4j2.properties
mv spark-env.sh.template spark-env.sh
mv workers.template workers
mv spark-defaults.conf.template spark-defaults.conf

```

编辑`spark-defaults.conf`

```properties
spark.master                       yarn
spark.eventLog.enabled             true
spark.eventLog.dir                 hdfs://master:9000/spark/eventLog
spark.serializer                   org.apache.spark.serializer.KryoSerializer
spark.driver.memory                1g
spark.executor.memory              2g
spark.submit.deployMode            cluster
spark.yarn.queue                   default
spark.yarn.maxAppAttempts          1
spark.driver.extraLibraryPath      /usr/local/hadoop/lib/native,/usr/local/hive/lib
spark.executor.extraLibraryPath    /usr/local/hadoop/lib/native,/usr/local/hive/lib
spark.sql.hive.metastore.version   3.1.3
spark.sql.hive.metastore.jars      path
spark.sql.hive.metastore.jars.path file:///usr/local/hive/lib/*.jar
```

编辑workers

```
slave1
slave2
slave3
```

### 使用xsync分发spark文件

### 创建spark日志文件夹

```sh
hdfs dfs -mkdir -p /spark/eventLog
```

### 为hive、hadoop配置项创建软链接

切换到spark的conf文件夹

```
ln -s $HADOOP_HOME/etc/hadoop/core-site.xml core-site.xml
ln -s $HADOOP_HOME/etc/hadoop/hdfs-site.xml hdfs-site.xml
ln -s $HIVE_HOME/conf/hive-site.xml hive-site.xml
```

### 测试

首先启动dfs和yarn。

```sh
./bin/spark-submit \
  --class org.apache.spark.examples.SparkPi \
  --master yarn \
  --deploy-mode cluster \
  --executor-memory 4G \
  --num-executors 3 \
  $SPARK_HOME/examples/jars/spark-examples_2.12-3.5.4.jar \
  1000
```

在yarn网页的日志里查看结果

提交Structured Streaming+kafka程序时要添加packages。

```sh
$SPARK_HOME/bin/spark-submit --class com.example.telecom.Main --master yarn --deploy-mode cluster --packages org.apache.spark:spark-sql-kafka-0-10_2.13:3.5.4 kafka-consumer-1.0-SNAPSHOT.jar
```

结束程序：

```sh
yarn application -kill <ID>
```

