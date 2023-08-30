package space.nexel.aether.core.platform

import space.nexel.aether.core.base.Ref

object Resource {
  trait Config {}
  trait Factory[T, C <: Config] {
    def apply(config: C): T
    def load(ref: Ref, config: C)(using dispatcher: Dispatcher): Resource[T] =
      Resource.error("Factory.load not supported")

    protected var resources = Set[T]()
    /*private[aether]*/ def released(resource: T) = {
      assert(resources.contains(resource))
      resources -= resource
    }
    def instances: Seq[T] = resources.toSeq
  }

  /** Create Resource indicating error. */
  def error[T](message: String)(using dispatcher: Dispatcher): Resource[T] = {
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
  private val listeners = collection.mutable.Set[Resource[T] => _]()

  def error_=(msg: String)(using dispatcher: Dispatcher) = {
    assert(res.isEmpty)
    assert(errorMsg.isEmpty)
    errorMsg = Some(msg)
    dispatcher.dispatch {
      listeners.foreach(_(this))
    }
  }

  def error = errorMsg

  def set(newRes: T)(using dispatcher: Dispatcher): Unit = {
    assert(res.isEmpty)
    assert(errorMsg.isEmpty)
    res = Some(newRes)
    dispatcher.dispatch {
      listeners.foreach(_(this))
    }
  }

  def get: Option[T] = res
  def apply(): T = res.get

  def onChange(listener: Resource[T] => _): Unit = {
    listeners += listener
    if (res.isDefined) listener(this)
  }

  def map[U](f: T => U)(using dispatcher: Dispatcher): Resource[U] = {
    val res = new Resource[U]()
    get match {
      case Some(value) => res.set(f(value))
      case None        => res.errorMsg = errorMsg
    }
    res
  }

  def flatMap[U](f: T => Resource[U])(using dispatcher: Dispatcher): Resource[U] = {
    val res = new Resource[U]()
    get match {
      case Some(value) => f(value).onChange(r => res.set(r()))
      case None        => res.errorMsg = errorMsg
    }
    res
  }

}
