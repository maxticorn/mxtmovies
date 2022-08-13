package server

import cats.syntax.apply._
import cats.instances.option._
import server.domain._
import server.domain.parameters._
import sttp.model.StatusCode
import sttp.tapir.json.circe._
import sttp.tapir.ztapir._

object endpoints {
  private val defaultError = jsonBody[BusinessError] and statusCode(StatusCode.Ok)
  private val paramsError  = jsonBody[ParamsError] and statusCode(StatusCode.BadRequest)

  private val baseEndpoint = endpoint
    .in("api")
    .errorOut(oneOf(oneOfVariant(defaultError), oneOfVariant(paramsError)))

  private val genreRequest =
    (query[Option[String]]("genre") and query[Option[Int]]("limit"))
      .map[Option[GenreRequest]]((_: (Option[String], Option[Int])).mapN(GenreRequest)) {
        _.map(req => (req.name, req.limit)).unzip
      }

  val title =
    baseEndpoint.get
      .in("title")
      .in(query[Option[TitleNameRequest]]("name"))
      .in(genreRequest)
      .out(jsonBody[MovieResponse])

  val kevinBaconDegree =
    baseEndpoint.get
      .in("kevin-bacon-degree")
      .in(path[String]("name"))
      .out(jsonBody[KevinBaconDegreeResponse])
}
