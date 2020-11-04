package collector

import collector.lib.DataFetcher

object Collector extends App {
  val fetcher = new DataFetcher()

  fetcher.getSpreads("XBTUSD")
}
