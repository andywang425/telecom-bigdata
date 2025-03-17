import org.apache.spark.ml.clustering.KMeans
import org.apache.spark.ml.feature.{StandardScaler, VectorAssembler}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{SaveMode, SparkSession}

object KMeans1 {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("UserClustering")
      .enableHiveSupport()
      .getOrCreate()

    import spark.implicits._

    val callTable = spark.table("telecom_data.call")
    val smsTable = spark.table("telecom_data.sms")
    val trafficTable = spark.table("telecom_data.traffic")

    val callDF = callTable
      .filter($"callDirection" === "OUTGOING")
      .withColumn("hour", hour($"callStartTime"))
      .cache()
    val smsDF = smsTable
      .filter($"sendDirection" === "SENT")
      .withColumn("hour", hour($"sendTime"))
      .cache()
    val trafficDF = trafficTable
      .withColumn("hour", hour($"sessionStartTime"))
      .cache()

    val callAgg = callDF
      .withColumnRenamed("callerNumber", "phone")
      .groupBy("phone")
      .agg(
        sum(when($"hour" >= 6 && $"hour" < 12, 1).otherwise(0)).alias("call_count_1"),
        sum(when($"hour" >= 12 && $"hour" < 20, 1).otherwise(0)).alias("call_count_2"),
        sum(when($"hour" >= 20 || $"hour" < 6, 1).otherwise(0)).alias("call_count_3"),
      )

    val smsAgg = smsDF
      .withColumnRenamed("senderNumber", "phone")
      .groupBy("phone")
      .agg(
        sum(when($"hour" >= 6 && $"hour" < 12, 1).otherwise(0)).alias("sms_count_1"),
        sum(when($"hour" >= 12 && $"hour" < 20, 1).otherwise(0)).alias("sms_count_2"),
        sum(when($"hour" >= 20 || $"hour" < 6, 1).otherwise(0)).alias("sms_count_3")
      )

    val trafficAgg = trafficDF
      .withColumnRenamed("userNumber", "phone")
      .groupBy("phone")
      .agg(
        sum(when($"hour" >= 6 && $"hour" < 12, 1).otherwise(0)).alias("session_count_1"),
        sum(when($"hour" >= 12 && $"hour" < 20, 1).otherwise(0)).alias("session_count_2"),
        sum(when($"hour" >= 20 || $"hour" < 6, 1).otherwise(0)).alias("session_count_3")
      )

    val userUsageDF = callAgg
      .join(smsAgg, Seq("phone"), "outer")
      .join(trafficAgg, Seq("phone"), "outer")
      .na.fill(0)

    userUsageDF.show(4096, truncate = false)

    val assembler = new VectorAssembler()
      .setInputCols(Array("call_count_1", "call_count_2", "call_count_3", "sms_count_1", "sms_count_2", "sms_count_3", "session_count_1", "session_count_2", "session_count_3"))
      .setOutputCol("features")

    val featureDF = assembler.transform(userUsageDF)

    val scaler = new StandardScaler()
      .setInputCol("features")
      .setOutputCol("scaledFeatures")
      .setWithStd(true) // 启用标准差缩放
      .setWithMean(true) // 启用中心化（Spark 3.0+ 支持稠密向量）

    val scalerModel = scaler.fit(featureDF)
    val scaledData = scalerModel.transform(featureDF)

    val kmeans = new KMeans()
      .setK(2)
      .setSeed(1L)
      .setFeaturesCol("scaledFeatures")
      .setPredictionCol("cluster")

    val model = kmeans.fit(scaledData)

    val predictions = model.transform(scaledData)
    predictions.show(4096, truncate = false)

    println("Cluster Centers: ")
    model.clusterCenters.foreach(center => println(center.toString))

    // 在打印标准化后的聚类中心后，添加原始中心计算
    println("Original Cluster Centers: ")
    val originalCenters = model.clusterCenters.map { scaledVector =>
      // 获取标准化时的均值和标准差（来自scalerModel）
      val mean = scalerModel.mean.toArray // 均值向量
      val std = scalerModel.std.toArray   // 标准差向量

      // 逐个特征维度反标准化
      val originalValues = scaledVector.toArray.zipWithIndex.map {
        case (scaledValue, idx) =>
          scaledValue * std(idx) + mean(idx)
      }

      // 返回可解释的向量
      org.apache.spark.ml.linalg.Vectors.dense(originalValues)
    }

    // 打印原始特征量纲的簇中心
    originalCenters.foreach(center => println(center))

    predictions.printSchema()

    predictions
      .select("phone", "cluster")
      .write
      .format("jdbc")
      .option("url", "jdbc:mysql://master:3306/telecom")
      .option("dbtable", "user_cluster")
      .option("user", "root")
      .option("password", "root")
      .option("driver", "com.mysql.cj.jdbc.Driver")
      .mode(SaveMode.Overwrite)
      .save()

    // 在现有代码的模型预测后添加PCA处理
    import org.apache.spark.ml.feature.PCA

    // 1. 创建PCA模型降维到2维
    val pca = new PCA()
      .setInputCol("scaledFeatures") // 使用标准化后的特征
      .setOutputCol("pcaFeatures")
      .setK(2) // 降维到2D

    val pcaModel = pca.fit(predictions)
    val pcaResult = pcaModel.transform(predictions)
      .select("phone", "pcaFeatures", "cluster")

    // 2. 提取PCA坐标并保存结果
    val pcaCoords = pcaResult.map { row =>
      val phone = row.getString(0)
      val pcaArray = row.getAs[org.apache.spark.ml.linalg.Vector](1).toArray
      val cluster = row.getInt(2)

      (phone, pcaArray(0), pcaArray(1), cluster)
    }.toDF("phone", "pca_x", "pca_y", "cluster")

    // 3. 保存结果到CSV（方便后续可视化）
    pcaCoords
      .coalesce(1) // 合并成单个文件
      .write
      .option("header", "true")
      .mode("overwrite")
      .csv("/user/root/telecom/pca_cluster_results")

    spark.stop()
  }
}
