package service

import _root_.config._
import server.domain.parameters._
import server.domain.{BusinessError, KevinBaconDegreeResponse, MovieResponse}
import service.domain.KevinBaconDegree
import zio._
import zio.cache.{Cache, Lookup}

import scala.concurrent.TimeoutException
import scala.language.postfixOps

trait ApiService {
  def getByTitle(title: TitleNameRequest): IO[BusinessError, MovieResponse]
  def topInGenre(genre: GenreRequest): IO[BusinessError, MovieResponse]
  def kevinBaconDegree(name: String): IO[BusinessError, KevinBaconDegreeResponse]
}

case class ApiServiceImpl(titles: TitleService,
                          serviceConfig: ServiceConfig,
                          cache: Cache[String, Throwable, List[KevinBaconDegree]]
) extends ApiService {
  def getByTitle(title: TitleNameRequest): IO[BusinessError, MovieResponse] =
    titles
      .getByTitle(title.value)
      .map(MovieResponse(_))
      .tapErrorCause(ZIO.logCause(_))
      .mapError(_ => BusinessError(""))

  def topInGenre(genre: GenreRequest): IO[BusinessError, MovieResponse] =
    titles
      .getTopByGenre(genre.name, genre.limit)
      .map(MovieResponse(_))
      .tapErrorCause(ZIO.logCause(_))
      .mapError(_ => BusinessError(""))

  def kevinBaconDegree(name: String): IO[BusinessError, KevinBaconDegreeResponse] =
    cache
      .get(name)
      .forkDaemon
      .timeoutFail(new TimeoutException)(serviceConfig.kevinBaconDegree.timeout)
      .flatMap(_.join)
      .map(KevinBaconDegreeResponse(_))
      .tapErrorCause(ZIO.logCause(_))
      .mapError(_ => BusinessError(""))
}

object ApiService {
  val live: ZLayer[ServiceConfig with TitleService, Nothing, ApiServiceImpl] =
    ZLayer.fromZIO(for {
      service    <- ZIO.service[TitleService]
      config     <- ZIO.service[ServiceConfig]
      cacheConfig = config.kevinBaconDegree.cache getOrElse CacheConfig(100, 100 seconds)
      cache <- Cache.makeWith(cacheConfig.capacity, Lookup(service.kevinBaconDegree)) {
                 case Exit.Success(_) => cacheConfig.timeToLive
                 case Exit.Failure(_) => 0 seconds
               }
    } yield ApiServiceImpl(service, config, cache))
}
