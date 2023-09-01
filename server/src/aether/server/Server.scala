package aether.server

import cats.effect.IO
import cats.effect.ExitCode
import scala.concurrent.ExecutionContext
import org.http4s.blaze.server.BlazeServerBuilder
import io.github.cdimascio.dotenv.Dotenv
import pureconfig.ConfigSource
import cats.effect.IOApp

object Server extends IOApp {

  Dotenv.configure().systemProperties().ignoreIfMissing().load()
  val config = ConfigSource.default.loadOrThrow[Config]
  val logger = org.log4s.getLogger

  def run(args: List[String]): IO[ExitCode] = {
    for {
      exitCode <- BlazeServerBuilder[IO](ExecutionContext.global)
        .bindHttp(config.port, config.interface)
        .withHttpApp(WebAppService().services.orNotFound)
        .serve.compile.drain.as(ExitCode.Success)
    } yield exitCode
  }
}
