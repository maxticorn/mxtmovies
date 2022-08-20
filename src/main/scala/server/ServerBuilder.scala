package server

import org.http4s.HttpRoutes
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.Server
import server.domain._
import server.domain.parameters._
import server.endpoints._
import service.ApiService
import sttp.tapir.server.http4s.ztapir.ZHttp4sServerInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.tapir.ztapir._
import zio.interop.catz._
import zio._
import _root_.config.ServerConfig

import scala.language.postfixOps

case class ServerBuilder(apiService: ApiService) {
  private val endpointsForSwagger =
    List(titles, titlesSearch, kevinBaconDegree)

  private val swaggerEndpoints: List[ZServerEndpoint[Any, Any]] =
    SwaggerInterpreter().fromEndpoints[Task](endpointsForSwagger, "imdb", "1.0")

  private val routes: HttpRoutes[Task] =
    ZHttp4sServerInterpreter()
      .from(
        titles.zServerLogic(apiService.topInGenre) ::
          titlesSearch.zServerLogic(apiService.getByTitle) ::
          kevinBaconDegree.zServerLogic(apiService.kevinBaconDegree) ::
          swaggerEndpoints
      )
      .toRoutes

  def server(serverConfig: ServerConfig): BlazeServerBuilder[Task] =
    BlazeServerBuilder[Task]
      .bindHttp(serverConfig.port, serverConfig.host)
      .withResponseHeaderTimeout(serverConfig.requestTimeout)
      .withHttpApp(routes.orNotFound)
      .withoutBanner
}

object ServerBuilder {
  val server: ZLayer[ServerConfig with ApiService, Throwable, Server] =
    ZLayer.scoped(for {
      config     <- ZIO.service[ServerConfig]
      apiService <- ZIO.service[ApiService]
      builder     = ServerBuilder(apiService)
      server     <- builder.server(config).resource.toScopedZIO
    } yield server)
}
