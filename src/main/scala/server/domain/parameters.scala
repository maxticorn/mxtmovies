package server.domain

object parameters {
  case class TitleNameRequest(value: String) extends AnyVal
  case class Filters(genre: String, limit: Int)
}
