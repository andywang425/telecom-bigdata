# 启动dfs和yarn
start-all.sh
# 关闭dfs和yarn
stop-all.sh
# hive启动
hive --service metastore
hive --service hiveserver2
# kafka启动
cd /usr/local/kafka
bin/kafka-server-start.sh config/kraft/server.properties
# 生成电信数据
java -jar telecom-data-generator-1.0.0.jar
# 运行kafka消费者
$SPARK_HOME/bin/spark-submit --class com.example.telecom.Main --master yarn --deploy-mode cluster --packages org.apache.spark:spark-sql-kafka-0-10_2.13:3.5.4 kafka-consumer-1.0-SNAPSHOT.jar
# 运行kafka生产者
cd ~/playground
java -jar kafka-producer-1.0-SNAPSHOT.jar output/call.csv output/sms.csv output/traffic.csv
# 终止kafka消费者
yarn application -kill <ID>
# 数据分析
$SPARK_HOME/bin/spark-submit data-analysis-1.0-SNAPSHOT.jar
# Spark ml
$SPARK_HOME/bin/spark-submit spark-ml-1.0-SNAPSHOT.jar