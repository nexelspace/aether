package space.nexel.aether.lib.util

import space.nexel.aether.core.types.Mat4F
import space.nexel.aether.core.math.VMathF
import space.nexel.aether.lib.types.Transform2F

object MatUtil {

  /**
   * Set orthogonal projection matrix in screen coordinates.
   */
  def orthoTransform(x1: Float, x2: Float, y1: Float, y2: Float): Transform2F = {
    val scaleX = 2 / (x2 - x1)
    val scaleY = 2 / (y2 - y1)
    Transform2F.identity.translate(-VMathF.sign(scaleX), -VMathF.sign(scaleY)).scale(scaleX, scaleY)
  }

  def ortho(x1: Float, x2: Float, y1: Float, y2: Float): Mat4F = orthoTransform(x1, x2, y1, y2).toMat4f

  /**
   * Same as gluPerspective.
   * @param fovyInDegrees
   * @param aspectRatio
   * @param znear
   * @param zfar
   */
  def perspective(fovyInDegrees: Float, aspectRatio: Float, znear: Float, zfar: Float): Mat4F = {
    val ymax = znear * VMathF.tan(fovyInDegrees * VMathF.Pi / 360.0f)
    val xmax = ymax * aspectRatio
    frustum(-xmax, xmax, -ymax, ymax, znear, zfar)
  }

  def perspectiveBalanced(fovDegrees: Float, sizeX: Float, sizeY: Float, znear: Float, zfar: Float): Mat4F = {
    val n = znear * VMathF.tan(fovDegrees * VMathF.Pi / 360.0f)
    val ratioSqrt = VMathF.sqrt(sizeX / sizeY)
    val ymax = n / ratioSqrt
    val xmax = n * ratioSqrt
    frustum(-xmax, xmax, -ymax, ymax, znear, zfar)
  }

  def frustum(nearX: Float, nearY: Float, znear: Float, zfar: Float): Mat4F = {
    val xmax = znear * nearX
    val ymax = znear * nearY
    frustum(-xmax, xmax, -ymax, ymax, znear, zfar)
  }

  def frustum(left: Float, right: Float, bottom: Float, top: Float, znear: Float, zfar: Float): Mat4F = {
    val temp = 2.0f * znear
    val temp2 = right - left
    val temp3 = top - bottom
    val temp4 = zfar - znear
    Mat4F(
      temp / temp2, 0, 0, 0,
      0, temp / temp3, 0, 0,
      (right + left) / temp2, (top + bottom) / temp3, (-zfar - znear) / temp4, -1,
      0, 0, (-temp * zfar) / temp4, 0)
  }

}