package collector.domain

case class KrakenResponse[R](error: Seq[String], result: R)