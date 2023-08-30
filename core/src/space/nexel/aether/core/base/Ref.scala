package space.nexel.aether.core.base

case class Ref(base: Base, segments: Seq[String]) {

  def this(base: Base, path: String) = this(base, path.split('/'))
  
}
