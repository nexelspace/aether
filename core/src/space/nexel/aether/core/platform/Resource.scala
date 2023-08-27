package space.nexel.aether.core.platform

object Resource {
  trait Config {}
  trait Factory[T, C <: Config] {
    def apply(config: C): T
    def load(url: String, config: C)(onload: Resource[T] => _): Resource[T] =
      Resource.error("Factory.load not supported")

    protected var resources = Set[T]()
    private[aether] def released(resource: T) = {
      assert(resources.contains(resource))
      resources -= resource
    }
    def instances: Seq[T] = resources.toSeq
  }

  /** Create Resource indicating error. */
  def error[T](message: String): Resource[T] = {
    val res = new Resource[T]()
    res.error = message
    res
  }
}

/** Asynchronously loaded resource. May result in error.
  */
class Resource[T]() {
  private var res: Option[T] = None
  private var errorMsg: Option[String] = None

  def error_=(msg: String) = {
    assert(res.isEmpty)
    assert(errorMsg.isEmpty)
    errorMsg = Some(msg)
  }

  def error = errorMsg

  def set(newRes: T): Unit = {
    assert(res.isEmpty)
    assert(errorMsg.isEmpty)
    res = Some(newRes)
  }

  def get: Option[T] = res

}
