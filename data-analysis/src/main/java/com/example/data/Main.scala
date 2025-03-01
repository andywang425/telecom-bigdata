package com.example.data

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object Main {

  def main(args: Array[String]): Unit = {
    // 创建sparkContext  --- 本地模式运行
    val conf = new SparkConf().setAppName("wordCount").setMaster("local[*]")
    val sc = new SparkContext(conf)

    // 加载文件
    val context: RDD[String] = sc.textFile("D:\\Documents\\毕业设计\\telecom-bigdata\\data-analysis\\src\\main\\resources\\words.txt")

    // 数据处理
    val split: RDD[String] = context.flatMap(item => item.split(" "))
    val count: RDD[(String, Int)] = split.map(item => (item, 1))
    val reduce = count.reduceByKey((curr, agg) => curr + agg)
    val result = reduce.collect()
    result.foreach(println(_))
  }

}
