package db

import doobie.Read

object domain {
  case class TitleBasics(tconst: String,
                         titleType: Option[String],
                         primaryTitle: Option[String],
                         originalTitle: Option[String],
                         isAdult: Option[Boolean],
                         startYear: Option[Int],
                         endYear: Option[Int],
                         runtimeMinutes: Option[Long],
                         genres: Option[String]
  )
  case class NameBasics(nconst: String,
                        primaryName: String,
                        birthYear: Option[Int],
                        deathYear: Option[Int],
                        primaryProfession: Option[String],
                        knownForTitles: Option[String]
  )
  case class TitleRating(averageRating: Option[Double], numVotes: Option[Long])
  case class Principal(nameBasics: NameBasics)
  case class CrewIdentifiers(directors: Option[List[String]], writers: Option[List[String]])
  object CrewIdentifiers {
    implicit val read: Read[CrewIdentifiers] =
      Read[(Option[String], Option[String])]
        .map { case (directors, writers) =>
          CrewIdentifiers(directors.map(_.split(",").toList), writers.map(_.split(",").toList))
        }
  }
  case class TitleCrew(directors: Option[List[NameBasics]], writers: Option[List[NameBasics]])
  case class Title(basics: TitleBasics, rating: TitleRating, principals: List[Principal], crew: TitleCrew)
}
