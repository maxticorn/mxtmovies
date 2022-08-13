package service

import db.Repo
import db.domain.NameBasics
import io.scalaland.chimney.dsl._
import service.domain._
import zio._
import zio.interop.catz._

import scala.language.postfixOps

trait TitleService {
  def getByTitle(title: String): Task[List[Title]]
  def getTopByGenre(genre: String, limit: Int): Task[List[Title]]
  def kevinBaconDegree(name: String): Task[List[KevinBaconDegree]]
}

case class TitleServiceImpl(repo: Repo) extends TitleService {
  def getByTitle(title: String): Task[List[Title]] =
    repo.getByTitle(title).map(_.transformInto[Title]).compile.toList

  def getTopByGenre(genre: String, limit: Int): Task[List[Title]] =
    repo.getTopByGenre(genre, limit).map(_.transformInto[Title]).compile.toList

  private def degree(name: NameBasics) =
    for {
      _      <- ZIO.log(s"found ${name.nconst}")
      result <- repo.kevinBaconDegree(name.nconst)
      _      <- ZIO.log(s"degree for ${name.nconst} = $result")
    } yield KevinBaconDegree(name.transformInto[Name], result)

  def kevinBaconDegree(name: String): Task[List[KevinBaconDegree]] =
    repo
      .findActor(name)
      .parEvalMap(4)(degree)
      .compile
      .toList
}

object TitleService {
  val live: ZLayer[Repo, Nothing, TitleService] =
    ZLayer.fromFunction(TitleServiceImpl.apply _)
}
