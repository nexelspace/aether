package aether.core.base

import scala.io.Source
import aether.core.platform.Resource
import Base.Entry
import aether.core.platform.Dispatcher
import io.circe.Json
import io.circe.parser.parse
import aether.core.platform.Log

class ResourceBase(cls: Class[_])(using dispatcher: Dispatcher) extends Base {
  def toUrl(path: String): String = ???

  def base(path: String): Base = ???

  def entry(path: String): Resource[Entry] = ???

  def list(pathDir: String): Resource[Seq[Entry]] = ???

  def loadString(path: String): Resource[String] = ???
  // {
  //   val pack = cls.getName()
  //   Log(s"Loading resource $path from $pack")
  //   cls.getResourceAsStream(path) match {
  //     case null   => Resource.error(s"Resource not found: $path")
  //     case stream => Resource(Source.fromInputStream(stream).mkString)
  //   }
  // }

  def loadBytes(path: String): Resource[Array[Byte]] = ???
  // cls.getResourceAsStream(path) match {
  //   case null   => Resource.error(s"Resource not found: $path")
  //   case stream => Resource(stream.readAllBytes())
  // }

  def loadJson(path: String): Resource[Json] = ???
  // loadString(path).flatMap { case string =>
  //   parse(string) match {
  //     case Left(error) => Resource.error(error.toString())
  //     case Right(json) => Resource(json)
  //   }
  // }
}
