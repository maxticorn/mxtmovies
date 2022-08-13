package db

import cats.data.NonEmptyList
import db.domain._
import doobie._
import doobie.implicits._

object SQL {
  private val selectAll =
    fr"""
        |select title_basics.*,
        |title_ratings.averageRating, title_ratings.numVotes,
        |title_crew.directors, title_crew.writers
      """.stripMargin

  def selectTitle(title: String): doobie.Query0[(TitleBasics, TitleRating, CrewIdentifiers)] =
    sql"""
         |$selectAll
         |from title_basics
         |left join title_ratings on title_basics.tconst = title_ratings.tconst
         |left join title_crew on title_basics.tconst = title_crew.tconst
         |where title_basics.primaryTitle = $title or title_basics.originalTitle = $title
         |;""".stripMargin
      .query[(TitleBasics, TitleRating, CrewIdentifiers)]

  def getPrincipals(tconst: String): doobie.Query0[Principal] =
    sql"""
         |select name_basics.*
         |from title_principals
         |inner join name_basics on title_principals.nconst = name_basics.nconst
         |where title_principals.tconst = $tconst
         |;""".stripMargin
      .query[NameBasics]
      .map(Principal)

  def getNames(nconsts: NonEmptyList[String]): doobie.Query0[NameBasics] =
    sql"""
         |select name_basics.*
         |from name_basics
         |where ${Fragments.in(fr"nconst", nconsts)}
         |;""".stripMargin
      .query[NameBasics]

  def selectByGenre(genre: String, limit: Int): doobie.Query0[(TitleBasics, TitleRating, CrewIdentifiers)] =
    sql"""
         |$selectAll
         |from title_basics
         |inner join title_ratings on title_basics.tconst = title_ratings.tconst
         |left join title_crew on title_basics.tconst = title_crew.tconst
         |where
         |title_ratings.numvotes > 100000
         |and genres ~ ('(?:^|,)' || ${genre.capitalize} || '(?:$$|,)')
         |order by title_ratings.averageRating desc
         |limit $limit
         |;""".stripMargin
      .query[(TitleBasics, TitleRating, CrewIdentifiers)]

  def findActor(name: String): doobie.Query0[NameBasics] =
    sql"""
         |select *
         |from name_basics
         |where primaryName = $name
         |;""".stripMargin
      .query[NameBasics]

  def kevinBaconDegree(nconst: String): doobie.Query0[Long] =
    sql"""
         |with recursive tconst_to_name as (
         |	select title_basics.tconst,
         |	       name_basics.primaryName,
         |	       name_basics.nconst
         |	from title_basics
         |	inner join title_principals on title_principals.tconst = title_basics.tconst
         |	inner join name_basics on name_basics.nconst = title_principals.nconst
         |	where title_principals.category = 'actor'
         |), ntn as (
         |	select tconst_to_name.nconst as n1, name_basics.nconst as n2
         |	from tconst_to_name
         |	inner join title_principals on title_principals.tconst = tconst_to_name.tconst
         |	inner join name_basics on name_basics.nconst = title_principals.nconst
         |	where tconst_to_name.primaryName != name_basics.primaryName
         |), names as (
         |  select nconst from name_basics
         |), p(last, destination, path, stop) as (
         |  select n_from.nconst,
         |	       n_to.nconst,
         |	       array[n_from.nconst::text],
         |	       n_from.nconst = n_to.nconst
         |	from names n_from, names n_to
         |	where n_from.nconst = 'nm0000102'
         |	and n_to.nconst = $nconst
         |	union all
         |	select ntn.n2,
         |		   p.destination,
         |		   p.path || ntn.n2::text,
         |	       bool_or(ntn.n2 = p.destination) over ()
         |	from    ntn inner join p on ntn.n1 = p.last
         |	where   ntn.n1 = p.last
         |	and not ntn.n2 = any(path)
         |	and not p.stop
         |)
         |select cardinality(path) - 1
         |from   p
         |where  p.last = $nconst
         |limit 1
         |;""".stripMargin
      .query[Long]
}
