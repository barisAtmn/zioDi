package com.baris

import com.baris.implementations.{ConfigurationService, UserPersistenceService}
import zio.{TaskLayer, ULayer}
import zio.blocking.Blocking
import zio.clock.Clock

object Environments {

  type AppEnvironment = ConfigurationModule with UserPersistanceModule

  val configuration: ULayer[ConfigurationModule] = ConfigurationService.live
  val dbTransactor: TaskLayer[UserPersistanceModule] = configuration ++  Blocking.live >>> UserPersistenceService.liveH2
  val appEnvironment: TaskLayer[AppEnvironment] = configuration ++ dbTransactor

}

