package com.baris.implementations

import com.baris.ConfigurationModule
import com.baris.interfaces.Configuration.{Config, Service}
import pureconfig.ConfigSource
import zio.{Task, ZIO, ZLayer}
import pureconfig.generic.auto._

// Implementation of Service
object ConfigurationService extends Service {
  override val load: Task[Config] = ZIO
    .fromEither(ConfigSource.default.load[Config])
    .mapError(failures =>
      new IllegalStateException(
        s"Error loading configuration: $failures"
      )
    )

  /**
   * Layer for DI
   */
  val live: ZLayer[Any, Nothing, ConfigurationModule] =
    ZLayer.succeed(ConfigurationService)

  /**
   * accessors
   **/
  def getConfig:ZIO[ConfigurationModule, Throwable, Config] = ZIO.accessM[ConfigurationModule](_.get.load)

}