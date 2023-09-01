package aether.core.base

import aether.core.base.Base
import aether.core.platform.*
import aether.core.graphics.Texture
import aether.core.network.HttpClient
import io.circe.Json
import aether.core.base.Base.Entry

class HttpBase(http: HttpClient, baseUrl: String)(using dispatcher: Dispatcher) extends Base {

  def toUrl(path: String): String = s"$baseUrl/$path"

  def base(path: String): Base = HttpBase(http, s"$baseUrl/$path")
  def entry(path: String): Resource[Entry] = ???
  def list(pathDir: String): Resource[Seq[Entry]] = ???

  def loadString(path: String): Resource[String] = http.loadString(toUrl(path))
  def loadTexture(path: String): Resource[Texture] = http.loadTexture(toUrl(path))
  def loadBytes(path: String): Resource[Array[Byte]] = http.loadBytes(toUrl(path))
  def loadJson(path: String): Resource[Json] = http.loadJson(toUrl(path))

}
