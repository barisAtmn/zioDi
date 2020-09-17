package com
import com.baris.implementations.{ConfigurationService, KafkaConsumerAndProducer}
import zio.Has
import com.baris.interfaces.{Configuration, Persistence}
import zio.blocking.Blocking
import zio.clock.Clock

package object baris {

  /**
   * Has enables us to define the implementation of the service on ZLayer this will make it easier to combine the dependencies of our App.
   * val database: ZIO[Configuration, Throwable, DB] == val database: ZIO[Has[Configuration.Service], Throwable, DB]
   *
   **/
  type ConfigurationModule = Has[Configuration.Service]

  type UserPersistanceModule = Has[Persistence.Service[User]]


  case class User(id: Int)

  case class UserNotFound(id: Int) extends Throwable

}
