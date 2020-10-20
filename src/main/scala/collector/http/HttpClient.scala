package collector.http

import io.circe.Decoder
import io.circe.parser.decode
import scala.concurrent.{Future, Promise}
import akka.http.scaladsl.model.{HttpMethod, Uri, HttpHeader, RequestEntity, HttpEntity, HttpRequest}
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.Http
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.http.scaladsl.unmarshalling.Unmarshal

class HttpClient {
  def request[T: Decoder](
    method: HttpMethod,
    url: String,
    query: Uri.Query = Query(),
    headers: Seq[HttpHeader] = Nil,
    entity: RequestEntity = HttpEntity.Empty
  ): Future[T] = {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer
    implicit val executionContext = system.dispatcher
    val promise = Promise[T]

    val response = Http().singleRequest(
      HttpRequest(
        method,
        Uri(url).withQuery(query),
        headers,
        entity
      )
    )
    val unmarshalled = response.flatMap { response =>
      Unmarshal(response.entity).to[String]
    }

    unmarshalled.flatMap { value =>
      // println(value)
      decode[T](value) match {
        case Right(t)  => promise.success(t).future
        case Left(err) => promise.failure(err).future
      }
    }
  }
}