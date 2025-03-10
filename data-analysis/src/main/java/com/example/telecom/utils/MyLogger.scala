package com.example.telecom.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

trait MyLogger {
  private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

  private def log(level: String, content: Any): Unit = {
    val dateTime = LocalDateTime.now.format(formatter)
    println(s"$dateTime $level ${this.getClass}: $content")
  }

  def info(content: Any): Unit = log("INFO", content)

  def warn(content: Any): Unit = log("WARN", content)

  def error(content: Any): Unit = log("ERROR", content)
}

