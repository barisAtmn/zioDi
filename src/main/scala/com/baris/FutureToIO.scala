package com.baris

import cats.effect.IO
import zio.{ZIO, ZLayer}

import scala.concurrent.Future

object FutureToIO {

  implicit val cs = IO.contextShift(scala.concurrent.ExecutionContext.global)

  // Interface
  trait Service[F[_],A] {
    def convert(value:F[A]): zio.Task[IO[A]]
  }

  case class FutureToIOImpl[F[_],A]() extends Service[Future,Any]{
    override def convert(value: Future[Any])= ZIO.effect(IO.fromFuture(IO.pure(value)))
  }

  // Layer for DI
  val live: ZLayer[Any, Nothing, FutureToIOModule] =
    ZLayer.succeed(FutureToIOImpl[Future,Any])

  def getConvert(value:Future[Any]):ZIO[FutureToIOModule, Throwable, IO[Any]] = ZIO.accessM[FutureToIOModule](_.get.convert(value))

}
