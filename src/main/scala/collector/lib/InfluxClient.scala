package collector.lib

import com.influxdb.client.InfluxDBClientFactory
import com.influxdb.client.InfluxDBClient
import collector.domain.BinanceCandlestick
import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.domain.DeletePredicateRequest
import java.time.OffsetDateTime

object InfluxClient {
  def apply(): InfluxClient = {
    val token =
    "v1amvSM05TBVThLFWVgp0VmVlupdHM82fz459Smzbhh9TXwufu7QFQES1xgCi6FMEkh_3R75Ut4uA_Nzxq_5YQ=="
    val org = "dodona"
    val bucket = "crypto"

    val client = InfluxDBClientFactory.create(
      "http://localhost:8086",
      token.toCharArray,
      org,
      bucket
    )
  
    new InfluxClient(client, bucket, org)
  }
}

class InfluxClient(client: InfluxDBClient, bucket: String, org: String) {
  private val writeApi = client.getWriteApi()
  private val deleteApi = client.getDeleteApi()
  
  def writeCandlestickToInflux(candle: BinanceCandlestick, pair: String, interval: String): Unit = {
    println("Writing", candle)
    writeApi.writeRecord(
      WritePrecision.MS,
      s"candlestick_interval,pair=$pair,interval=$interval open=${candle.open},high=${candle.high},low=${candle.low},close=${candle.close},volume=${candle.volume} ${candle.closeTime}"
    )
  }

  def deleteData(prd: String): Unit = {
    // Delete endpoint does not seem to be implemented by Influx yet...
    val start = "1970-01-01T00:00:00.00Z"
    val stop = "2020-10-19T00:00:00.00Z"
    deleteApi.delete(OffsetDateTime.parse(start), OffsetDateTime.parse(stop), prd, bucket, org)
  }

  def close(): Unit = {
    client.close()
  }
}
