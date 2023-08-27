package space.nexel.aether.js.sys

import org.scalajs.dom.XMLHttpRequest
import scala.scalajs.js

import scala.concurrent.Future
import scala.concurrent.Promise

import concurrent.ExecutionContext.Implicits.global
import io.circe.Json
import io.circe._
import io.circe.parser._

object JsHttpClient {

  def loadBytes(url: String): Future[Array[Byte]] = new Loader(url).loadBytes()
  def loadJson(url: String): Future[js.Object] = new Loader(url).loadJson()
  def loadHeaders(url: String): Future[Map[String, String]] = new Loader(url).loadHeaders()

  import scala.scalajs.js.typedarray._

  class Loader(url: String) {
    val req = new XMLHttpRequest()
    def error[A](promise: Promise[A]) =
      promise.failure(new RuntimeException(s"Failed to load $url - ${req.status} ${req.statusText}"))

    def loadBytes(): Future[Array[Byte]] = {
      var promise = Promise[Array[Byte]]()
      req.open("GET", url, true)
      req.responseType = "arraybuffer"
      req.onload = { event =>
        if (req.status == 200) {
          val array = int8Array2ByteArray(new Int8Array(req.response.asInstanceOf[ArrayBuffer]))
          promise.success(array)
        } else error(promise)
      }
      req.onerror = { event => error(promise) }
      req.send()
      promise.future

    }

    def loadJson(): Future[js.Object] = {
      var promise = Promise[js.Object]()
      req.open("GET", url, true)
      req.responseType = "json"
      req.onload = { event =>
        if (req.status == 200) {
          val json = req.response.asInstanceOf[js.Object]
          promise.success(json)
        } else error(promise)
      }
      req.onerror = { event => error(promise) }
      req.send()
      promise.future

    }

    def loadHeaders(): Future[Map[String, String]] = {
      var promise = Promise[Map[String, String]]()
      req.open("HEAD", url, true)
      req.onload = { event =>
        val headers = req.getAllResponseHeaders()
        val arr = headers.trim().split("\r?\n|\r").toList
        val kv = for (line <- arr) yield {
          val kv = line.split(": ")
          kv(0) -> kv(1)
        }
        promise.success(kv.toMap)
      }
      req.onerror = { event => error(promise) }
      req.send()
      promise.future
    }
  }
}

class JsHttpClient {

  def headers(url: String): Future[Map[String, String]] = {
    JsHttpClient.loadHeaders(url)
  }

  def loadString(url: String): Future[String] = {
    JsHttpClient.loadBytes(url).map {
      array => new String(array, "UTF8").replace("\\r\\n", "\n").replaceAll("\\r", "\n")
    }
  }

  def loadBytes(url: String): Future[Array[Byte]] =
    JsHttpClient.loadBytes(url)

  def loadJson(url: String): Future[Json] = {
    loadString(url).map(parse(_).right.get)
  }

  def loadJsonAs[T](url: String)(implicit d: Decoder[T]): Future[T] = {
    loadString(url).map { string =>
      parse(string).right.get.as[T] match {
        case Left(message) => throw RuntimeException(s"Failed to decode json: $string, $message")
        case Right(obj)    => obj
      }
    }
  }

  def postJson(url: String, content: Json): Future[Boolean] = {
    send("POST", url, content)
  }

  def send(method: String, url: String, content: Json = Json.Null): Future[Boolean] = {
    var promise = Promise[Boolean]()
    val req = new XMLHttpRequest()
    req.open(method, url, true)
    req.setRequestHeader("Content-Type", "application/json")
    req.onload = { event =>
      if (req.status >= 200 && req.status < 300) {
        promise.success(true)
      } else {
        promise.failure(new RuntimeException(s"Failed to post $url - ${req.status} ${req.statusText}"))
      }
    }
    req.onerror = { event =>
      promise.failure(new RuntimeException(s"Failed to post $url - ${req.status} ${req.statusText}"))
    }
    req.send(content.toString)
    promise.future
  }
}
