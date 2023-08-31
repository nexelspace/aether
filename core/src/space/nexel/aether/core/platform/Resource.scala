package space.nexel.aether.core.platform

import space.nexel.aether.core.base.Ref
import javax.sound.midi.spi.MidiFileReader
import scala.collection.mutable.ArrayBuffer

object Resource {
  trait Config {}
  trait Factory[T, C <: Config] {
    protected var resources = Set[T]()
    def instances: Seq[T] = resources.toSeq

    final def create(config: C): T = {
      val obj = createThis(config)
      resources += obj
      obj
    }
    final def load(ref: Ref, config: C)(using dispatcher: Dispatcher): Resource[T] = {
      val res = loadThis(ref, config)
      res.onChange { r =>
        resources += r()
      }
      res
    }
    def createThis(config: C): T
    def loadThis(ref: Ref, config: C)(using dispatcher: Dispatcher): Resource[T] =
      Resource.error("Factory.load not supported")

    private[aether] def released(resource: T) = {
      assert(resources.contains(resource))
      resources -= resource
    }

  }

  /** Create Resource indicating error. */
  def error[T](message: String)(using dispatcher: Dispatcher): Resource[T] = {
    val res = new Resource[T]()
    res.error = message
    res
  }

  def apply[T](value: T)(using dispatcher: Dispatcher): Resource[T] = {
    val res = new Resource[T]()
    res.set(value)
    res
  }

  def sequence[T](seq: Seq[Resource[T]])(using dispatcher: Dispatcher): Resource[Seq[T]] = {
    val res = new Resource[Seq[T]]()
    val values = ArrayBuffer[T]()
    var error: Option[String] = None
    var count = seq.size
    seq.foreach { r =>
      r.onChange { r =>
        r.error match {
          case Some(msg) =>
            error = Some(msg)
            count -= 1
            if (count == 0) {
              if (error.isDefined) res.error = error.get
              else res.set(values.toSeq)
            }
          case None =>
            values += r()
            count -= 1
            if (count == 0) {
              if (error.isDefined) res.error = error.get
              else res.set(values.toSeq)
            }
        }
      }
    }
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

  def onChange(listener: Resource[T] => _): Resource[T] = {
    listeners += listener
    if (res.isDefined || error.isDefined) listener(this)
    this
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

  override def toString = if (res.isDefined) s"Resource:${res.get}"
  else if (error.isDefined) s"Error:${error.get}"
  else "Loading"
}
