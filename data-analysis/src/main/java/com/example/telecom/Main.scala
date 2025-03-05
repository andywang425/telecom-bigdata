package com.example.telecom

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object Main {

  def main(args: Array[String]): Unit = {
    // 创建sparkContext  --- 本地模式运行
    val conf = new SparkConf().setAppName("test").setMaster("local[*]")
    val sc = new SparkContext(conf)

    // 加载文件
    val context: RDD[String] = sc.textFile("D:\\Documents\\毕业设计\\telecom-bigdata\\telecom-data-generator\\output\\call.csv")

    val temp: Array[String] = context.collect().drop(1)
    val data: RDD[String] = sc.parallelize(temp)

    val r1: RDD[(String, String)] = data.keyBy(_.split(",")(0))
    val r2 = r1.map(item => (item._1, item._2.split(",").drop(1)))

    println(r2.collect().mkString("\n"))

    // 数据处理
    //    val split: RDD[String] = context.flatMap(item => item.split(" "))
    //    val count: RDD[(String, Int)] = split.map(item => (item, 1))
    //    val reduce = count.reduceByKey((curr, agg) => curr + agg)
    //    val result = reduce.collect()
    //    result.foreach(println(_))
  }

}
