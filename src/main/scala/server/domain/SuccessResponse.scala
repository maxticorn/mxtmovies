package server.domain

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import service.domain.{KevinBaconDegree, Name, Title}
import sttp.tapir.Schema

sealed trait SuccessResponse

case class MovieResponse(titles: List[Title]) extends SuccessResponse
object MovieResponse {
  implicit val encoder: Encoder[MovieResponse]    = deriveEncoder[MovieResponse].mapJson(_.deepDropNullValues)
  implicit val decoder: Decoder[MovieResponse]    = deriveDecoder
  implicit lazy val schema: Schema[MovieResponse] = Schema.derived
}

case class KevinBaconDegreeResponse(degrees: List[KevinBaconDegree]) extends SuccessResponse
object KevinBaconDegreeResponse {
  implicit val encoder: Encoder[KevinBaconDegreeResponse] =
    deriveEncoder[KevinBaconDegreeResponse].mapJson(_.deepDropNullValues)
  implicit val decoder: Decoder[KevinBaconDegreeResponse]    = deriveDecoder
  implicit lazy val schema: Schema[KevinBaconDegreeResponse] = Schema.derived
}
