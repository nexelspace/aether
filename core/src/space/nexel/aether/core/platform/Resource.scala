package space.nexel.aether.core.platform

import space.nexel.aether.core.base.Ref
import javax.sound.midi.spi.MidiFileReader
import scala.collection.mutable.ArrayBuffer
import Resource.*
import scala.annotation.nowarn

object Resource {

  enum State[T] {
    case Loading()
    case Error(msg: String)
    case Loaded(value: T)
  }

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

  /** Convert Seq[Resource[T]] to Resource[Seq[T]]
    *
    * @param seq
    * @param dispatcher
    * @return
    */
  def sequence[T](seq: Seq[Resource[T]])(using dispatcher: Dispatcher): Resource[Seq[T]] = {
    val res = new Resource[Seq[T]]()
    var error: Option[String] = None
    var loadCount = seq.map(r => if (r.state == State.Loading()) 1 else 0).reduce(_ + _)
    def loaded() = {
      val error = seq.find(_.error.isDefined).flatMap(_.error)
      if (error.isDefined) {
        res.error = error.get
      } else {
        res.set(seq.map(_()))
      }
    }
    if (loadCount == 0) {
      loaded()
    } else {
      seq.foreach {
        case r if (r.state == State.Loading()) =>
          r.onChange { _ =>
            loadCount -= 1
            if (loadCount == 0) loaded()
          }
        case _ =>
      }
    }
    res
  }

}

/** Asynchronously loaded resource. May result in error.
  */
class Resource[T]() {

  private var state: State[T] = State.Loading()

  private val listeners = collection.mutable.Set[Resource[T] => _]()

  def error_=(msg: String)(using dispatcher: Dispatcher) = {
    assert(state == State.Loading())
    state = State.Error(msg)
    dispatcher.dispatch {
      listeners.foreach(_(this))
    }
  }

  def error = state match {
    case State.Error(msg) => Some(msg)
    case _                => None
  }

  def get: Option[T] = state match {
    case State.Loaded[T](value) => Some(value)
    case _                      => None
  }

  def apply(): T = get.get

  def set(newRes: T)(using dispatcher: Dispatcher): Unit = {
    assert(state == State.Loading())
    state = State.Loaded(newRes)
    dispatcher.dispatch {
      listeners.foreach(_(this))
    }
  }

  def onChange(listener: Resource[T] => _): Resource[T] = {
    listeners += listener
    if (state != State.Loading()) listener(this)
    this
  }

  def map[U](f: T => U, res: Resource[U] = new Resource[U]())(using dispatcher: Dispatcher): Resource[U] = {
    state match {
      case State.Error(msg)    => res.error = msg
      case State.Loaded(value) => res.set(f(value))
      case State.Loading()     => onChange(_ => map(f, res))
    }
    res
  }

  def flatMap[U](f: T => Resource[U], res: Resource[U] = new Resource[U]())(using
      dispatcher: Dispatcher
  ): Resource[U] = {
    state match {
      case State.Error(msg)       => res.error = msg
      case State.Loaded[T](value) => f(value).onChange(r => res.set(r()))
      case State.Loading()        => onChange(_ => flatMap(f, res))
    }
    res
  }

  override def toString = s"Resource:$state"
}
