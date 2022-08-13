package server.domain

object parameters {
  case class TitleNameRequest(value: String) extends AnyVal
  case class GenreRequest(name: String, limit: Int)
}
