package aether.js.platform

import aether.core.network.HttpClient
import aether.core.platform.*
import aether.core.graphics.Texture
import org.scalajs.dom.XMLHttpRequest
import scala.scalajs.js

import scala.concurrent.Future
import scala.concurrent.Promise

import concurrent.ExecutionContext.Implicits.global
import io.circe.Json
import io.circe._
import io.circe.parser._

object JsHttpClient {

  def loadBytes(url: String)(using dispatcher: Dispatcher): Resource[Array[Byte]] = new Loader(url).loadBytes()
  def loadJson(url: String)(using dispatcher: Dispatcher): Resource[Json] = new Loader(url).loadJson()
  def loadHeaders(url: String)(using dispatcher: Dispatcher): Resource[Map[String, String]] =
    new Loader(url).loadHeaders()

  import scala.scalajs.js.typedarray._

  class Loader(url: String)(using dispatcher: Dispatcher) {
    val req = new XMLHttpRequest()
    def error = s"Failed to load $url - ${req.status} ${req.statusText}"

    def load[T](method: String, resType: Option[String])(onload: Resource[T] => Unit): Resource[T] = {
      val res = new Resource[T]
      req.open(method, url, true)
      resType.foreach(t => req.responseType = t)
      req.onload = { event =>
        onload(res)
      }
      req.onerror = { event => res.error = error }
      req.send()
      res
    }

    def loadBytes(): Resource[Array[Byte]] = {
      load("GET", Some("arraybuffer")) { res =>
        if (req.status == 200) {
          val array = int8Array2ByteArray(new Int8Array(req.response.asInstanceOf[ArrayBuffer]))
          res.set(array)
        } else res.error = error
      }
    }

    def loadJson(): Resource[Json] = {
      load("GET", Some("json")) { res =>
        if (req.status == 200) {
          val json = req.response.asInstanceOf[Json]
          res.set(json)
        } else res.error = error
      }
    }

    def loadHeaders(): Resource[Map[String, String]] = {
      load("HEAD", None) { res =>
        val headers = req.getAllResponseHeaders()
        val arr = headers.trim().split("\r?\n|\r").toList
        val kv = for (line <- arr) yield {
          val kv = line.split(": ")
          kv(0) -> kv(1)
        }
        res.set(kv.toMap)
      }
    }
  }
}

class JsHttpClient(using dispatcher: Dispatcher) extends HttpClient {

  def headers(url: String): Resource[Map[String, String]] = {
    JsHttpClient.loadHeaders(url)
  }

  def loadString(url: String): Resource[String] = {
    JsHttpClient.loadBytes(url).map { array =>
      new String(array, "UTF8").replace("\\r\\n", "\n").replaceAll("\\r", "\n")
    }
  }

  def loadTexture(url: String): Resource[Texture] = ???

  def loadBytes(url: String): Resource[Array[Byte]] =
    JsHttpClient.loadBytes(url)

  def loadJson(url: String): Resource[Json] = {
    loadString(url).map(parse(_).right.get)
  }

  def loadJsonAs[T](url: String)(implicit d: Decoder[T]): Resource[T] = {
    loadString(url).map { string =>
      parse(string).right.get.as[T] match {
        case Left(message) => throw RuntimeException(s"Failed to decode json: $string, $message")
        case Right(obj)    => obj
      }
    }
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
