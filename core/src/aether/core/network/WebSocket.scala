package aether.core.network

import WebSocket.*
import aether.core.buffers.ByteBuffer
import aether.core.platform.*

object WebSocket {
  
  case class Config(url: String) extends Resource.Config

  // def open(url: String): WebSocket = factory.create(Config(url))

  abstract class WebSocketEvent extends Event {
    def socket: WebSocket
  }
  case class OnConnect(socket: WebSocket) extends WebSocketEvent
  case class OnStringMessage(socket: WebSocket, message: String) extends WebSocketEvent
  case class OnBufferMessage(socket: WebSocket, message: ByteBuffer) extends WebSocketEvent
  case class OnError(socket: WebSocket, message: String) extends WebSocketEvent
  case class OnClose(socket: WebSocket, code: Int, reason: String) extends WebSocketEvent
}

trait WebSocket extends NativeResource[WebSocket, WebSocket.Config] {

  def isConnected: Boolean
  def send(message: String): Unit
  def send(message: ByteBuffer): Unit
  def close(): Unit
}
