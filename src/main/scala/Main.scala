import db.Repo
import org.http4s.server.Server
import server.ServerBuilder
import service._
import zio._
import _root_.config._

object Main extends ZIOAppDefault {
  def run: Task[Nothing] =
    ZIO
      .serviceWithZIO[Server](_ => ZIO.never)
      .provide(
        Repo.live,
        Repo.transactor,
        DBConfig.live,
        TitleService.live,
        ApiService.live,
        ServiceConfig.live,
        ServerBuilder.server,
        ServerConfig.live
      )
}
