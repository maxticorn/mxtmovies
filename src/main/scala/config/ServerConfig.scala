package config

import zio.ZLayer
import zio.config.magnolia.descriptor
import zio.config.{PropertyTreePath, ReadError, read}
import zio.config.typesafe.TypesafeConfigSource

import scala.concurrent.duration.Duration

case class ServerConfig(host: String, port: Int, requestTimeout: Duration)
object ServerConfig {
  val live: ZLayer[Any, ReadError[String], ServerConfig] =
    ZLayer(read {
      descriptor[ServerConfig].from(
        TypesafeConfigSource.fromResourcePath
          .at(PropertyTreePath.$("server"))
      )
    })
}
