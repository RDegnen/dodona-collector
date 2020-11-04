package collector.domain

case class KrakenSpread(time: Long, bid: BigDecimal, ask: BigDecimal)

case class KrakenSpreadsWithSymbol(symbol: String, spreads: Seq[KrakenSpread])