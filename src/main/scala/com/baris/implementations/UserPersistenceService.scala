package com.baris.implementations

import cats.effect.Blocker

import com.baris.{ConfigurationModule, User, UserPersistanceModule}
import com.baris.interfaces.Persistence
import doobie.h2.H2Transactor
import zio.{Managed, Task, ZIO, ZLayer, ZManaged}
import zio.blocking.blocking

import scala.concurrent.ExecutionContext
import doobie.{Query0, Transactor, Update0}
import doobie.implicits._
import zio.blocking.Blocking
import zio.interop.catz._

case class UserPersistenceService(tnx: Transactor[Task]) extends Persistence.Service[User] {

  import UserPersistenceService.SQL

  override def get(id: Int): Task[User] = SQL
    .get(id)
    .option
    .transact(tnx)
    .foldM(
      err => Task.fail(err),
      maybeUser => Task.succeed(maybeUser.get)
    )

  override def create(user: User): Task[User] = SQL
    .create(user)
    .run
    .transact(tnx)
    .foldM(err => Task.fail(err), _ => Task.succeed(user))

  override def delete(id: Int): Task[Boolean] = SQL
    .delete(id)
    .run
    .transact(tnx)
    .fold(_ => false, _ => true)

   override def createDB: Task[Boolean] = SQL
    .createDB
    .run
    .transact(tnx)
    .fold(_ => false, _ => true)
}

object UserPersistenceService {

  import com.baris.implementations.ConfigurationService._
  import com.baris.interfaces.Configuration.Config

  def mkTransactor(
                    conf: Config,
                    connectEC: ExecutionContext,
                    transactEC: ExecutionContext
                  ):Managed[Throwable,UserPersistenceService] = {

    val transactor:ZManaged[Any, Throwable, H2Transactor[Task]] =
      H2Transactor.newH2Transactor[Task](
        conf.dbConfig.url,
        conf.dbConfig.user,
        conf.dbConfig.password,
        connectEC,
        Blocker.liftExecutionContext(transactEC)
      )
      .toManagedZIO

    transactor.map(new UserPersistenceService(_))
  }

  object SQL {

    import doobie.implicits._

    def get(id: Int): Query0[User] =
      sql"""SELECT * FROM USERS WHERE ID = $id """.query[User]

    def create(user: User): Update0 =
      sql"""INSERT INTO USERS (id) VALUES (${user.id})""".update

    def delete(id: Int): Update0 =
      sql"""DELETE FROM USERS WHERE id = $id""".update

    def createDB: Update0 =
      sql"""CREATE TABLE USERS (id int)""".update
  }

  val live: ZLayer[ConfigurationModule with Blocking, Throwable, UserPersistanceModule] =
    ZLayer.fromManaged (
      for {
        config <- getConfig.toManaged_
        connectEC <- ZIO.descriptor.map(_.executor.asEC).toManaged_
        blockingEC <- blocking { ZIO.descriptor.map(_.executor.asEC)
        }.toManaged_
        managed <- mkTransactor(config, connectEC, blockingEC)
      } yield managed
    )

  /**
   * accessors
   **/
  def UserPersistance_get(id:Int):ZIO[UserPersistanceModule, Throwable, User] = ZIO.accessM[UserPersistanceModule](_.get.get(id))

  def UserPersistance_create(user:User):ZIO[UserPersistanceModule, Throwable, User] = ZIO.accessM[UserPersistanceModule](_.get.create(user))

  def UserPersistance_delete(id: Int):ZIO[UserPersistanceModule, Throwable, Boolean] = ZIO.accessM[UserPersistanceModule](_.get.delete(id))

  def UserPersistance_createDB:ZIO[UserPersistanceModule, Throwable, Boolean] = ZIO.accessM[UserPersistanceModule](_.get.createDB)




}
