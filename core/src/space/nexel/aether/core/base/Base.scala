package space.nexel.aether.core.base

trait Base {
  def ref(path: String): Ref = new Ref(this, path)
}
