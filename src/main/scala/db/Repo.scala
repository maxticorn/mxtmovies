package db

import cats.data.NonEmptyList
import cats.syntax.traverse._
import cats.instances.option._
import config.DBConfig
import db.domain.{CrewIdentifiers, TitleBasics, TitleRating, _}
import doobie._
import doobie.hikari.HikariTransactor
import doobie.implicits._
import doobie.util.ExecutionContexts
import zio.interop.catz._
import zio._

trait Repo {
  def getByTitle(title: String): fs2.Stream[Task, Title]
  def getTopByGenre(genre: String, limit: Int): fs2.Stream[Task, Title]
  def findActor(name: String): fs2.Stream[Task, NameBasics]
  def kevinBaconDegree(nconst: String): Task[Long]
}

case class RepoImpl(xa: Transactor[Task]) extends Repo {
  private def getNames(nconsts: Option[List[String]]) =
    nconsts.flatMap(NonEmptyList.fromList).traverse(SQL.getNames(_).to[List])

  private def constructTitle(basics: TitleBasics, rating: TitleRating, crewIdentifiers: CrewIdentifiers) =
    for {
      principals <- SQL.getPrincipals(basics.tconst).to[List]
      writers    <- getNames(crewIdentifiers.writers)
      directors  <- getNames(crewIdentifiers.directors)
    } yield Title(basics, rating, principals, TitleCrew(writers, directors))

  def getByTitle(title: String): fs2.Stream[Task, Title] =
    SQL
      .selectTitle(title)
      .stream
      .evalMap((constructTitle _).tupled)
      .transact(xa)

  def getTopByGenre(genre: String, limit: Int): fs2.Stream[Task, Title] =
    SQL
      .selectByGenre(genre, limit)
      .stream
      .evalMap((constructTitle _).tupled)
      .transact(xa)

  def findActor(name: String): fs2.Stream[Task, NameBasics] =
    SQL.findActor(name).stream.transact(xa)

  def kevinBaconDegree(nconst: String): Task[Long] =
    SQL.kevinBaconDegree(nconst).unique.transact(xa)
}

object Repo {
  val transactor: ZLayer[DBConfig, Throwable, Transactor[Task]] =
    ZLayer.scoped(for {
      cfg <- ZIO.service[DBConfig]
      ce  <- ExecutionContexts.fixedThreadPool[Task](10).toScopedZIO
      xa <- HikariTransactor
              .newHikariTransactor[Task]("org.postgresql.Driver", cfg.url, cfg.user, cfg.password, ce)
              .toScopedZIO
    } yield xa)

  val live: ZLayer[Transactor[Task], Throwable, Repo] =
    ZLayer.fromFunction(RepoImpl.apply _)
}
