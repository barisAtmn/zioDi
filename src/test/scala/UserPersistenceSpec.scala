import com.baris.{User, UserPersistanceModule}
import com.baris.implementations.{ConfigurationService, UserPersistenceService}
import com.baris.implementations.UserPersistenceService.{UserPersistance_create, UserPersistance_createDB}
import zio.{ExitCode, UIO, URIO, ZIO, ZLayer}
import zio.blocking.Blocking
import zio.console.{Console, putStrLn}

object UserPersistenceSpec extends zio.App {

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
  private val prepareEnvironment:UIO[ZLayer[Any, Throwable, UserPersistanceModule]] = UIO.succeed((Blocking.live ++ ConfigurationService.live) >>> UserPersistenceService.liveH2)
}
