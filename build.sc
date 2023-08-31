import mill._
import scalalib._
import mill.scalajslib.ScalaJSModule

val scalaV = "3.3.0"
val circeV = "0.14.3"
val http4sV = "1.0.0-M38"

object core extends ScalaModule {
  def scalaVersion = scalaV

  def ivyDeps = Agg(
    ivy"io.circe::circe-core::$circeV",
    ivy"io.circe::circe-generic::$circeV",
    ivy"io.circe::circe-parser::$circeV",
  )

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
//    ivy"io.lemonlabs::scala-uri:4.0.3",
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
  def moduleDeps = Seq(core, app)

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

def copy(from: os.Path, toDir: os.Path) = {
  os.list(from).map(f => os.copy.over(f, toDir / f.last, createFolders = true))
}

object server extends ScalaModule {
  def scalaVersion = scalaV
  def moduleDeps = Seq(core, app)
  // def scalacOptions = Seq("-Wunused:imports")
  def mainClass = Some("space.nexel.aether.server.Server")
  def resources = T.sources {
    val out = os.pwd / "out" / "server" / "resources"
    copy(js.fastLinkJS().dest.path, out / "scripts")
    // copy(os.pwd / "static", out / "static")
    Seq(
      millSourcePath / "resources",
      out
    ).map(p => PathRef(p))
  }

  def ivyDeps = Agg(
    ivy"org.http4s::http4s-core:$http4sV",
    ivy"org.http4s::http4s-blaze-server:$http4sV",
    ivy"org.http4s::http4s-blaze-client:$http4sV",
    ivy"org.http4s::http4s-circe:$http4sV",
    ivy"org.http4s::http4s-dsl:$http4sV",
    ivy"io.github.cdimascio:dotenv-java:2.3.2",
    ivy"com.github.pureconfig::pureconfig-core:0.17.2",
    ivy"ch.qos.logback:logback-classic:1.4.5".withDottyCompat(scalaVersion()),
    ivy"com.lihaoyi::scalatags:0.12.0",
    ivy"org.neo4j.driver:neo4j-java-driver:5.7.0",
    // ivy"io.github.neotypes::neotypes-core:0.23.3".withDottyCompat(scalaVersion()),
    ivy"io.github.neotypes::neotypes-generic:0.23.3".withDottyCompat(scalaVersion()),
    ivy"io.github.neotypes::neotypes-cats-effect:0.23.3".withDottyCompat(scalaVersion()),
    ivy"io.github.neotypes::neotypes-fs2-stream:0.23.3".withDottyCompat(scalaVersion()),
  )

  // Compile also js project
  override def compile = T {
    js.fastLinkJS()
    super.compile()
  }

}
