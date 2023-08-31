package space.nexel.aether.jvm.platform

import space.nexel.aether.core.base.Base
import space.nexel.aether.core.platform.Resource
import space.nexel.aether.core.graphics.Texture
import io.circe.Json
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.Files
import collection.JavaConverters.*
import space.nexel.aether.core.platform.Dispatcher
import io.circe.parser.*
import Base.*
import java.io.File

class FileBase(basePath: String)(using dispatcher: Dispatcher) extends Base {
  def toUrl(path: String): String = s"file://$path"

  def base(path: String): Base = FileBase(s"$basePath/$path")

  private def toNio(path: String): Path = Paths.get(s"$basePath/$path")
  private def toEntry(file: File): Entry = if (file.isDirectory()) Entry.Dir(file.getName()) else Entry.File(file.getName())
  private def handleException[T](res: => Resource[T]) = {
    try {
      res
    } catch {
      case e: Exception => Resource.error(e.toString())
    }
  }

  def entry(path: String): Resource[Entry] = handleException {
    Resource(toEntry(toNio(path).toFile()))
  }

  def list(pathDir: String): Resource[Seq[Entry]] = handleException {
    val files = toNio(pathDir).toFile().listFiles().toSeq
    Resource(files.map(toEntry))
  }

  def loadString(path: String): Resource[String] = handleException {
    val string = Files.readAllLines(toNio(path)).asScala.mkString("\n")
    Resource(string)
  }

  def loadTexture(path: String): Resource[Texture] = ???

  def loadBytes(path: String): Resource[Array[Byte]] = handleException {
    val bytes = Files.readAllBytes(toNio(path))
    Resource(bytes)
  }

  def loadJson(path: String): Resource[Json] = loadString(path).flatMap { case string =>
    parse(string) match {
      case Left(error) => Resource.error(error.toString())
      case Right(json) => Resource(json)
    }
  }
}

