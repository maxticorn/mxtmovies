package config

import zio._
import zio.config._
import zio.config.magnolia._
import zio.config.typesafe._

case class DBConfig(url: String, user: String, password: String)
object DBConfig {
  val live: ZLayer[Any, ReadError[String], DBConfig] =
    ZLayer(read {
      descriptor[DBConfig].from(
        TypesafeConfigSource.fromResourcePath
          .at(PropertyTreePath.$("db"))
      )
    })
}
