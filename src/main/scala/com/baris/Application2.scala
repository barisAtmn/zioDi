package com.baris

import com.baris.implementations._
import zio.blocking._
import zio._
import zio.clock.Clock
import zio.console.Console._

object Application2 extends App {
  def run(args: List[String]): ZIO[ZEnv, Nothing, ExitCode] = {
    val program = for {
      _ <- BusinessLogic.run
    } yield ()

    program.provideLayer(Environments.appEnvironment ++ Clock.live ++ Blocking.live ++ console.Console.live).exitCode
  }
}
