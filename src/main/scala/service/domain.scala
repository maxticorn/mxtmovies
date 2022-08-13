package service

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.scalaland.chimney._
import io.scalaland.chimney.dsl._
import sttp.tapir.Schema

object domain {
  case class Title(primaryTitle: Option[String],
                   rating: Option[Double],
                   startYear: Option[Int],
                   principals: List[Principal],
                   writers: Option[List[Name]],
                   directors: Option[List[Name]]
  )
  object Title {
    implicit val fromDb: Transformer[db.domain.Title, Title] =
      title =>
        Title(
          title.basics.primaryTitle,
          title.rating.averageRating,
          title.basics.startYear,
          title.principals.map(p => Principal(p.nameBasics.transformInto[Name])),
          title.crew.writers.map(_.map(_.transformInto[Name])),
          title.crew.directors.map(_.map(_.transformInto[Name]))
        )
    implicit val encoder: Encoder[Title]    = deriveEncoder[Title]
    implicit val decoder: Decoder[Title]    = deriveDecoder
    implicit lazy val schema: Schema[Title] = Schema.derived
  }

  case class Principal(name: Name)
  object Principal {
    implicit val encoder: Encoder[Principal]    = deriveEncoder[Principal]
    implicit val decoder: Decoder[Principal]    = deriveDecoder
    implicit lazy val schema: Schema[Principal] = Schema.derived
  }

  case class Name(primaryName: String, birthYear: Option[Int], deathYear: Option[Int])
  object Name {
    implicit val encoder: Encoder[Name]    = deriveEncoder[Name]
    implicit val decoder: Decoder[Name]    = deriveDecoder
    implicit lazy val schema: Schema[Name] = Schema.derived
  }

  case class KevinBaconDegree(name: Name, degree: Long)
  object KevinBaconDegree {
    implicit val encoder: Encoder[KevinBaconDegree]    = deriveEncoder[KevinBaconDegree]
    implicit val decoder: Decoder[KevinBaconDegree]    = deriveDecoder
    implicit lazy val schema: Schema[KevinBaconDegree] = Schema.derived
  }
}
