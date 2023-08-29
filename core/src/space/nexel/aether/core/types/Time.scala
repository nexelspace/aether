package space.nexel.aether.core.types

import java.util.Date

/**
 * TODO: For handling and formatting, see https://cquiroz.github.io/scala-java-time/
 */
object Time {
  def now: Time = {
    new Time(System.currentTimeMillis().toDouble/1000.0)
  }

  def apply(time_ms: Long) = new Time(time_ms/1000.0)

  val Undefined = Time(Double.NaN)

  // val format  = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss")
}

case class Time(seconds: Double) {
  def isUndefined = seconds.isNaN

  // override def toString: String = Time.format.format((seconds*1000.0).toLong) // No support in scala.js
}
