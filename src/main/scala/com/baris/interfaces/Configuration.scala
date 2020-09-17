package com.baris.interfaces

import com.baris.ConfigurationModule
import pureconfig.ConfigSource
import zio.{Task, ZIO, ZLayer}

object Configuration {

  case class Config(api: ApiConfig, dbConfig: DbConfig, kafka: KafkaConfig)
  case class ApiConfig(endpoint: String, port: Int)
  case class DbConfig(url: String, user: String, password: String)
  case class KafkaConfig(bootstrapServers: String, inputTopic: String, outputTopic:String)


  // Interface
  trait Service {
    val load: Task[Config]
  }


}
