package com.baris

import cats.effect.IO
import com.baris.Configuration._
import com.baris.FutureToIO._
import zio.{UIO, ZIO, ZLayer, console}
import zio.console._

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

object MyApp extends zio.App {

  def run(args: List[String]) = {
    val a:ZIO[Console, Nothing, Unit] = for {
      env <- prepareEnvironment
      out <- myAppLogic.provideLayer(env).foldM(
        error => console.putStrLn(s"Execution failed with: $error")
        ,config => console.putStrLn(config._2.unsafeRunSync().toString)
      )
    } yield out
     a.exitCode
  }



  val myAppLogic:ZIO[ConfigurationModule with Console with FutureToIOModule, Throwable, (Config, IO[Any])] = {
    implicit val myContext:ExecutionContext = ExecutionContext.global
    for {
      b <- getConvert(Future(4))
      a <- getConfig
      _ <- console.putStrLn(a.dbConfig.url)
    } yield (a, b)
  }


  private val prepareEnvironment = UIO.succeed(Configuration.live ++ Console.live ++ FutureToIO.live)

}