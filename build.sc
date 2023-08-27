import mill._
import scalalib._
import mill.scalajslib.ScalaJSModule

val scalaV = "3.3.0"
val circeV = "0.14.3"

object core extends ScalaModule {
  def scalaVersion = scalaV
}

object app extends ScalaModule {
  def scalaVersion = scalaV
  def moduleDeps = Seq(core)
}

object js extends ScalaJSModule {
  def scalaVersion = scalaV
  def scalaJSVersion = "1.13.0"

  // moduleDeps can't be used, so include sources from all modules
  val paths = Seq("core", "app", "js")
  def sources = T.sources(paths.map(p => PathRef(os.pwd / os.RelPath(p) / "src")))

  // must include dependencies from api & core projects
  def ivyDeps = Agg(
    ivy"io.lemonlabs::scala-uri:4.0.3",
    ivy"io.circe::circe-core::$circeV",
    ivy"io.circe::circe-generic::$circeV",
    ivy"io.circe::circe-parser::$circeV",
    ivy"org.scala-js::scalajs-dom::2.4.0",
    ivy"com.lihaoyi::scalatags::0.12.0"
  )
}

object jvm extends ScalaModule {
  def scalaVersion = scalaV
  // def scalacOptions = Seq("-Wunused:imports")
  def moduleDeps = Seq(core)

  def ivyDeps = {
    val lwjglVersion = "3.1.6"
    val platform = System.getProperty("os.name").split(" ")(0).toLowerCase match {
      case "mac" => "macos"
      case x     => x // linux, mac, windows, sunos
    }
    Agg(
    ) ++ Agg("lwjgl", "lwjgl-glfw", "lwjgl-opengl", "lwjgl-opengles").flatMap {
      case module =>
        Seq(
          ivy"org.lwjgl:$module:$lwjglVersion",
          ivy"org.lwjgl:$module:$lwjglVersion;classifier=natives-$platform"
        )
    }
  }
}