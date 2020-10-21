package collector.lib.db.schema

import slick.lifted.Tag
import slick.driver.SQLiteDriver.api._

case class Candlestick(
  id: Int,
  symbol: String,
  interval: String,
  open: BigDecimal,
  high: BigDecimal,
  low: BigDecimal,
  close: BigDecimal,
  volume: BigDecimal,
  openTime: Long,
  closeTime: Long
)

class Candlesticks(tag: Tag) extends Table[(Int, String, String, BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigDecimal, Long, Long)](tag, "candlesticks") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def symbol = column[String]("symbol")
  def interval = column[String]("interval")
  def open = column[BigDecimal]("open")
  def high = column[BigDecimal]("high")
  def low = column[BigDecimal]("low")
  def close = column[BigDecimal]("close")
  def volume = column[BigDecimal]("volume")
  def openTime = column[Long]("open_time")
  def closeTime = column[Long]("close_time")
  def * = (id, symbol, interval, open, high, low, close, volume, openTime, closeTime)
}