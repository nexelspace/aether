package aether.core.network

import aether.core.platform.*
import aether.core.graphics.Texture
import io.circe.Json

object HttpClient {
  case class Config(host: String, port: Int = 80) extends Resource.Config
}

trait HttpClient /*extends NativeResource[HttpClient, HttpClient.Config]*/ {
  def headers(url: String): Resource[Map[String, String]]

  def loadString(url: String): Resource[String]
  def loadTexture(url: String): Resource[Texture]
  def loadBytes(url: String): Resource[Array[Byte]]
  def loadJson(url: String): Resource[Json]

}
