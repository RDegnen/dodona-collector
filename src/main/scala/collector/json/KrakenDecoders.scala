package collector.json

import io.circe.Decoder
import io.circe.generic.semiauto._
import collector.domain.KrakenResponse
import collector.domain.KrakenSpreadsWithSymbol
import collector.domain.KrakenSpread
import io.circe.DecodingFailure

object KrakenDecoders {
  implicit def KrakenResponseDecoder[T: Decoder]: Decoder[KrakenResponse[T]] =
    deriveDecoder

  val symbolMatcher = Map("XXBTZUSD" -> "BTCUSD")

  lazy implicit val KrakenSpreadDecoder: Decoder[KrakenSpread] = deriveDecoder
  lazy implicit val KrakenSpreadsWithSymbolDecoder: Decoder[KrakenSpreadsWithSymbol] =
    Decoder.instance { hc =>
      val resultsSymbolKey = hc.downField("result").keys.flatMap(_.headOption)
      resultsSymbolKey.map(key => {
        val resultsList = hc
          .downField("result")
          .downField(key)
          .as[List[(Long, BigDecimal, BigDecimal)]]

        resultsList.map {
          case head :: next => {
            val spreads = KrakenSpread(head._1, head._2, head._3) +: 
              next.map(spread => KrakenSpread(spread._1, spread._2, spread._3))

            KrakenSpreadsWithSymbol(symbolMatcher(key), spreads)
          }
          case Nil => KrakenSpreadsWithSymbol(symbolMatcher(key), List())
        }
      }).getOrElse(Left(DecodingFailure("Failure", List())))
    }
}
