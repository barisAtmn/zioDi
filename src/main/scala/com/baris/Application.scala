package com.baris

import com.baris.implementations.UserPersistenceService._
import zio.{ExitCode, UIO, URIO, ZIO}
import com.baris.implementations._
import zio.blocking._
import zio.console._

object Application extends zio.App {

  def run(args: List[String]):URIO[zio.ZEnv with Console, ExitCode] = {
    (for {
      env <- prepareEnvironment
      out <- myAppLogic.provideCustomLayer(env).foldM(
        error => putStrLn(s"Execution failed with: $error"),
        success => putStrLn(success.id.toString)
      )
    } yield out).exitCode
  }

  val myAppLogic:ZIO[UserPersistanceModule, Throwable, User] = {
    val user = User(1)
    for {
      _        <- UserPersistance_createDB
      created  <- UserPersistance_create(user)
    } yield created
  }

  // Put all modules to ZIO context -- DI
  private val prepareEnvironment = UIO.succeed((Blocking.live ++ ConfigurationService.live) >>> UserPersistenceService.live)

}