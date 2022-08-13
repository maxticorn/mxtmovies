package config

import zio.{Duration, ZLayer}
import zio.config.magnolia.descriptor
import zio.config.typesafe.TypesafeConfigSource
import zio.config.{PropertyTreePath, ReadError, read}

case class ServiceConfig(kevinBaconDegree: MethodConfig)
case class MethodConfig(timeout: Duration, cache: Option[CacheConfig])
case class CacheConfig(capacity: Int, timeToLive: Duration)
object ServiceConfig {
  val live: ZLayer[Any, ReadError[String], ServiceConfig] =
    ZLayer(read {
      descriptor[ServiceConfig].from(
        TypesafeConfigSource.fromResourcePath
          .at(PropertyTreePath.$("service"))
      )
    })
}
