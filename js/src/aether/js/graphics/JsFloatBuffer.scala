package aether.js.graphics

import org.scalajs.dom.raw.{WebGLRenderingContext => GL}
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.typedarray.Float32Array

class JsFloatBuffer(seq: Seq[Float])(using gl: GL) {
  val array = Float32Array.from(seq.toJSArray)
  val buffer = gl.createBuffer()
  gl.bindBuffer(GL.ARRAY_BUFFER, buffer)
  gl.bufferData(GL.ARRAY_BUFFER, array, GL.STATIC_DRAW)

}
