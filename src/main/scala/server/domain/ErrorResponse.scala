package server.domain

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import sttp.tapir.Schema

sealed trait ErrorResponse

case class BusinessError(message: String) extends ErrorResponse
object BusinessError {
  implicit val encoder: Encoder[BusinessError]    = deriveEncoder
  implicit val decoder: Decoder[BusinessError]    = deriveDecoder
  implicit lazy val schema: Schema[BusinessError] = Schema.derived
}

case class ParamsError(message: String) extends ErrorResponse
object ParamsError {
  implicit val encoder: Encoder[ParamsError]    = deriveEncoder
  implicit val decoder: Decoder[ParamsError]    = deriveDecoder
  implicit lazy val schema: Schema[ParamsError] = Schema.derived
}
