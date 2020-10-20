package collector.json

import io.circe.Decoder
import io.circe.generic.semiauto._
import collector.domain.BinanceCandlestick

object Decoders {
  lazy implicit val BinanceCandlestickResponseDecoder: Decoder[BinanceCandlestick] =
  Decoder.instance { c =>
    // This decodes a list of HttpCandleStickResponses,
    // but not a single one. Still figuring out circe...
    val openTimec = c.downArray
    for {
      openTime <- openTimec.as[Long]
      openc = openTimec.right
      open <- openc.as[BigDecimal]
      highc = openc.right
      high <- highc.as[BigDecimal]
      lowc = highc.right
      low <- lowc.as[BigDecimal]
      closec = lowc.right
      close <- closec.as[BigDecimal]
      volumec = closec.right
      volume <- volumec.as[BigDecimal]
      closeTimec = volumec.right
      closeTime <- closeTimec.as[Long]
    } yield BinanceCandlestick(openTime, open, high, low, close, volume, closeTime)
  }
}