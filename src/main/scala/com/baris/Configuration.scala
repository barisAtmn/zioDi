package com.baris

import zio.{Task, ZIO, ZLayer}
import pureconfig._
import pureconfig.generic.auto._

object Configuration {

  case class Config(api: ApiConfig, dbConfig: DbConfig)
  case class ApiConfig(endpoint: String, port: Int)
  case class DbConfig(url: String, user: String, password: String)


  // Interface
  trait Service {
    val load: Task[Config]
  }

  // Implementation of Service
  object ServiceImpl extends Service {
    override val load: Task[Config] = Task.effect(ConfigSource.default.load[Config].right.get)
  }

  // Layer for DI
  val live: ZLayer[Any, Nothing, ConfigurationModule] =
    ZLayer.succeed(ServiceImpl)

  def getConfig:ZIO[ConfigurationModule, Throwable, Config] = ZIO.accessM[ConfigurationModule](_.get.load)

}
