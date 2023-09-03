package aether.app.canvas

import aether.core.platform.Platform
import aether.core.platform.Resource
import aether.core.platform.Dispatcher

class Resources(platform: Platform) {
  given Dispatcher = platform.dispatcher

  val resBase = platform.resource(this)
  val vertSource = resBase.loadString("vertex-default.vs")
  val fragSource = resBase.loadString("quad.fs")

  val allRes = Resource.sequence(Seq(vertSource, fragSource))

  def loaded = allRes.isLoaded
}
