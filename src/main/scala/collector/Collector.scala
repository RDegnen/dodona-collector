package collector

import com.influxdb.client.InfluxDBClientFactory
import com.influxdb.client.WriteApi
import akka.actor.ActorSystem
import collector.http.HttpClient
import collector.domain.BinanceCandlestick
import akka.http.scaladsl.model.HttpMethods
import collector.json.Decoders._
import akka.http.scaladsl.model.Uri.Query
import scala.util.Success
import scala.util.Failure
import com.influxdb.client.write.Point
import java.{util => ju}
import com.influxdb.client.domain.WritePrecision
import java.math.MathContext
import scala.math.BigDecimal.RoundingMode
import java.time.ZonedDateTime
import java.time.ZoneId
import collector.lib.InfluxClient
import slick.lifted.TableQuery
import collector.lib.db.schema.Candlesticks
import slick.driver.SQLiteDriver.api._
import collector.lib.db.DB

object Collector extends App {
  implicit val system = ActorSystem()
  implicit val executionContext = system.dispatcher
  val pair = "BTCUSD"
  val interval = "15m"

  val influxClinet = InfluxClient()
  val httpClient = new HttpClient()
  val request = httpClient.request[List[BinanceCandlestick]](
    HttpMethods.GET,
    s"${Constants.BINANCE_API_URL}/api/v3/klines",
    Query(Map("symbol" -> pair, "interval" -> interval, "limit" -> "1000"))
  )

  request.onComplete {
    case Success(value) => {
      val candles = TableQuery[Candlesticks]
      var candlesSeq: Seq[(Int, String, String, BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigDecimal, Long, Long)] = Seq.empty
      value.foreach(bc => {
        val tuple = (0, pair, interval, bc.open, bc.high, bc.low, bc.close, bc.volume, bc.openTime, bc.closeTime)
        candlesSeq :+= tuple
      })
      val insertAction = candles ++= candlesSeq
      DB.insert(insertAction)(executionContext)
      DB.close()
      system.terminate()
    }
    case Failure(exception) => {
      println(exception)
    }
  }
}
