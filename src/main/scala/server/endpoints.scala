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

  val titlesBase = baseEndpoint.get.in("titles")

  val titles =
    titlesBase
      .in((query[String]("genre") and query[Int]("limit")).mapTo[Filters])
      .out(jsonBody[MovieResponse])

  val titlesSearch =
    titlesBase
      .in("search")
      .in(query[TitleNameRequest]("name"))
      .out(jsonBody[MovieResponse])

  val kevinBaconDegree =
    baseEndpoint.get
      .in("kevin-bacon-degree")
      .in(path[String]("name"))
      .out(jsonBody[KevinBaconDegreeResponse])
}
