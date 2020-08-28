package com

import com.baris.Configuration.Config
import zio.ZIO

import scala.concurrent.Future

package object baris {

  /**
   * Has enables us to define the implementation of the service on ZLayer this will make it easier to combine the dependencies of our App.
   * val database: ZIO[Configuration, Throwable, DB] == val database: ZIO[Has[Configuration.Service], Throwable, DB]
   *
   **/
  type ConfigurationModule = zio.Has[Configuration.Service]

  type FutureToIOModule = zio.Has[FutureToIO.Service[Future,Any]]

}
