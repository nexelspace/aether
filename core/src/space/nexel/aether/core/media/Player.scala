package space.nexel.aether.core.media

import Player._
import scala.collection.mutable.Buffer
import space.nexel.aether.core.platform.NativeResource
import space.nexel.aether.core.platform.Resource

object Player {

  sealed abstract class State
  case object Stop extends State
  case object Play extends State
  
  case class Config() extends Resource.Config

  abstract class Track[T] {
    def position: Long = ???
    def length: Long = ???
    // val buffers: collection.Seq[T]
    def add(buffer: T, position: Option[Long]): Unit = ???
    def clear(): Unit = ???
    def release(): Unit = ???
  }

  class AudioTrack() extends Track[AudioBuffer] {
  }

  class VideoTrack() extends Track[VideoBuffer] {

  }

}

trait Player extends NativeResource[Player, Player.Config] {

  /** Position in seconds. */
  //var position: Double
  /** Duration in seconds. */
  //def duration: Double

  def tracks: List[Track[AudioBuffer | VideoBuffer]]
  def createAudioTrack(): AudioTrack
  def createVideoTrack(): VideoTrack

  var looping: Boolean
  //def state: State

  def play(): Unit

  def pause(): Unit

  def stop(): Unit

}
