package collector.lib.db

import slick.driver.SQLiteDriver.api._
import collector.lib.db.schema.Candlesticks
import scala.util.Success
import scala.util.Failure
import scala.concurrent.ExecutionContext

object DB {
  private val db = Database.forConfig("db")
  
  def insert(insertAction: DBIO[Option[Int]])(implicit ec: ExecutionContext): Unit = {
    val insertFuture = db.run(insertAction)

    insertFuture.onComplete {
      case Success(value) => println(value)
      case Failure(exception) => throw exception
    }
  }

  def close(): Unit = {
    db.close()
  }
}