package collector.domain

case class BinanceCandlestick(
  openTime:                 Long,
  open:                     BigDecimal,
  high:                     BigDecimal,
  low:                      BigDecimal,
  close:                    BigDecimal,
  volume:                   BigDecimal,
  closeTime:                Long,
  // quoteAssetVolume:         String,
  // numberOfTrades:           Long,
  // takerBuyBaseAssetVolume:  String,
  // takerBuyQuoteAssetVolume: String
)