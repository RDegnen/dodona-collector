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

object Collector extends App {
  implicit val system = ActorSystem()
  implicit val executionContext = system.dispatcher

  val token =
    "v1amvSM05TBVThLFWVgp0VmVlupdHM82fz459Smzbhh9TXwufu7QFQES1xgCi6FMEkh_3R75Ut4uA_Nzxq_5YQ=="
  val org = "dodona"
  val bucket = "crypto"
  val pair = "BTCUSD"
  val interval = "15m"

  val influxClient = InfluxDBClientFactory.create(
    "http://localhost:8086",
    token.toCharArray,
    org,
    bucket
  )
  val writeApi = influxClient.getWriteApi()
  val httpClient = new HttpClient()
  val request = httpClient.request[List[BinanceCandlestick]](
    HttpMethods.GET,
    s"${Constants.BINANCE_API_URL}/api/v3/klines",
    Query(Map("symbol" -> pair, "interval" -> interval, "limit" -> "1000"))
  )

  request.onComplete {
    case Success(value) => {
      value.foreach(writeCandlestickToInflux)
      influxClient.close()
      system.terminate()
    }
    case Failure(exception) => {
      println(exception)
    }
  }

  def writeCandlestickToInflux(candle: BinanceCandlestick): Unit = {
    println("Writing", candle)
    writeApi.writeRecord(
      WritePrecision.MS,
      s"candlestick_interval,pair=$pair,interval=$interval open=${candle.open},high=${candle.high},low=${candle.low},close=${candle.close},volume=${candle.volume} ${candle.closeTime}"
    )
  }
}
