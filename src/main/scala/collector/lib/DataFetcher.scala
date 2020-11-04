package collector.lib

import akka.actor.ActorSystem
import collector.http.HttpClient
import collector.lib.db.DB
import collector.domain.BinanceCandlestick
import akka.http.scaladsl.model.HttpMethods
import collector.Constants
import akka.http.scaladsl.model.Uri.Query
import scala.util.Success
import collector.lib.db.schema.Candlesticks
import scala.util.Failure
import slick.lifted.TableQuery
import collector.json.Decoders._
import collector.json.KrakenDecoders._
import slick.driver.SQLiteDriver.api._
import collector.domain.KrakenSpreadsWithSymbol
import collector.lib.db.schema.Spreads

class DataFetcher {
  implicit val system = ActorSystem()
  implicit val executionContext = system.dispatcher
  private val httpClient = new HttpClient()
  private val db = DB

  def getCandlestickData(pair: String, interval: String): Unit = {
    val request = httpClient.request[List[BinanceCandlestick]](
      HttpMethods.GET,
      s"${Constants.BINANCE_API_URL}/api/v3/klines",
      Query(Map("symbol" -> pair, "interval" -> interval, "limit" -> "1000"))
    )

    request.onComplete {
      case Success(values) => {
        val candles = TableQuery[Candlesticks]
        val candlesSeq = values.map(bc => (0, pair, interval, bc.open, bc.high, bc.low, bc.close, bc.volume, bc.openTime, bc.closeTime))
        DB.insert(candles ++= candlesSeq)(executionContext)
      }
      case Failure(exception) => {
        println(exception)
      }
    }
  }

  def getSpreads(pair: String): Unit = {
    val request = httpClient.request[KrakenSpreadsWithSymbol](
      HttpMethods.GET,
      s"${Constants.KRAKEN_API_URL}/0/public/Spread",
      Query(Map("pair" -> pair))
    )

    request.onComplete {
      case Success(value) => {
        val spreads = TableQuery[Spreads]
        val spreadsSeq = value.spreads.map(spread => (0, spread.time, spread.bid, spread.ask, value.symbol))
        DB.insert(spreads ++= spreadsSeq)(executionContext)
      }
      case Failure(exception) => println(exception)
    }
  }
}
