package aether.jvm.graphics

import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL12.*
import org.lwjgl.opengl.GL13.*
import org.lwjgl.opengl.GL14.*
import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GL21.*
import org.lwjgl.opengl.GL30.*
import org.lwjgl.opengl.GL31.*
import org.lwjgl.opengl.GL40.*
import org.lwjgl.opengl.GL41.*
import org.lwjgl.opengles.EXTTextureCompressionS3TC.*
import org.lwjgl.opengles.IMGTextureCompressionPVRTC.*
import org.lwjgl.opengles.OESCompressedETC1RGB8Texture.*

import scala.util.Failure
import scala.util.Success
import scala.util.Try
import aether.core.graphics.Texture.Format

object GlUtil {

  def autoCloseTry[A <: AutoCloseable, B](
    closeable: A)(fun: (A) => B): Try[B] = {

    Try(fun(closeable)).transform(
      result => {
        closeable.close()
        Success(result)
      },
      funT => {
        Try(closeable.close()).transform(
          _ => Failure(funT),
          closeT => {
            funT.addSuppressed(closeT)
            Failure(funT)
          })
      })
  }
  
  def toGlPixelFormat(format: Format): Int = format match {
    case Format.RGBA_8888   => GL_RGBA
//    case Format.LUMINANCE_8 => GL_LUMINANCE
//    case Format.BGRA_8888   => GL_BGRA
//    case Format.BGR_888     => GL_BGR
//    case Format.RGB_888     => GL_RGB
//    case Format.DEPTH_16    => GL_DEPTH_COMPONENT16
//    case Format.DEPTH_24    => GL_DEPTH_COMPONENT24
//    case Format.DEPTH_32    => GL_DEPTH_COMPONENT32
//    case Format.DEPTH_32F   => GL_DEPTH_COMPONENT32F
    case _                  => assert(false, s"Unsupported pixel format: $format"); 0
  }

  def toGlInternalFormat(format: Format): Int = format match {
    case Format.R32UI          =>  GL_R32UI //GL_RED
    case Format.RGBA_8888      => GL_RGBA
    case Format.RGBA_8888_UI    => GL_RGBA8UI
//    case Format.LUMINANCE_8    => GL_LUMINANCE
//    case Format.RG_16          => GL_RG
//    case Format.BGRA_8888      => GL_RGBA
//    case Format.BGR_888        => GL_RGB
//    case Format.RGB_888        => GL_RGB
//    case Format.SRGB_888       => GL_SRGB
//    case Format.DEPTH_16       => GL_RGB
//    case Format.DEPTH_24       => GL_RGB
//    case Format.DEPTH_32       => GL_RGB
//    case Format.DEPTH_32F      => GL_DEPTH_COMPONENT
//    case Format.ETC1           => GL_ETC1_RGB8_OES
//    case Format.PVRTC_4BIT     => GL_COMPRESSED_RGBA_PVRTC_4BPPV1_IMG
//    case Format.PVRTC_2BIT     => GL_COMPRESSED_RGBA_PVRTC_2BPPV1_IMG
//    case Format.PVRTC_4BIT_RGB => GL_COMPRESSED_RGB_PVRTC_4BPPV1_IMG
//    case Format.PVRTC_2BIT_RGB => GL_COMPRESSED_RGB_PVRTC_2BPPV1_IMG
//    case Format.DXT1           => GL_COMPRESSED_RGBA_S3TC_DXT1_EXT
//    case Format.DXT3           => GL_COMPRESSED_RGBA_S3TC_DXT3_EXT
//    case Format.DXT5           => GL_COMPRESSED_RGBA_S3TC_DXT5_EXT
    case _                     => sys.error(s"Unsupported pixel format: $format")
  }

  def toGlFormat(format: Format): Int = format match {
    case Format.R32UI          =>  GL_RED_INTEGER
    case Format.RGBA_8888      => GL_RGBA
    case Format.RGBA_8888_UI   => GL_RGBA_INTEGER
    case _                     => sys.error(s"Unimplemented for $format")
  }

  def getComponentType(format: Format) = format match {
    case Format.RGBA_8888 => GL_UNSIGNED_BYTE
    case Format.R32UI     => GL_UNSIGNED_INT
    case Format.RGBA_8888_UI     => GL_UNSIGNED_BYTE
//    case Format.DEPTH_16  => GL_UNSIGNED_SHORT
//    case Format.DEPTH_24  => GL_UNSIGNED_INT
//    case Format.DEPTH_32  => GL_UNSIGNED_INT
//    case Format.DEPTH_32F => GL_FLOAT
//    case Format.RG_16     => GL_UNSIGNED_SHORT
    case _                => sys.error(s"Unimplemented for $format")
  }

  val constantTypeIndex: Map[Int, String] = Map(
    GL_FLOAT -> "GL_FLOAT",
    GL_FLOAT_VEC2 -> "GL_FLOAT_VEC2",
    GL_FLOAT_VEC3 -> "GL_FLOAT_VEC3",
    GL_FLOAT_VEC4 -> "GL_FLOAT_VEC4",
    GL_DOUBLE -> "GL_DOUBLE",
    GL_DOUBLE_VEC2 -> "GL_DOUBLE_VEC2",
    GL_DOUBLE_VEC3 -> "GL_DOUBLE_VEC3",
    GL_DOUBLE_VEC4 -> "GL_DOUBLE_VEC4",
    GL_INT -> "GL_INT",
    GL_INT_VEC2 -> "GL_INT_VEC2",
    GL_INT_VEC3 -> "GL_INT_VEC3",
    GL_INT_VEC4 -> "GL_INT_VEC4",
    GL_UNSIGNED_INT -> "GL_UNSIGNED_INT",
    GL_UNSIGNED_INT_VEC2 -> "GL_UNSIGNED_INT_VEC2",
    GL_UNSIGNED_INT_VEC3 -> "GL_UNSIGNED_INT_VEC3",
    GL_UNSIGNED_INT_VEC4 -> "GL_UNSIGNED_INT_VEC4",
    GL_BOOL -> "GL_BOOL",
    GL_BOOL_VEC2 -> "GL_BOOL_VEC2",
    GL_BOOL_VEC3 -> "GL_BOOL_VEC3",
    GL_BOOL_VEC4 -> "GL_BOOL_VEC4",
    GL_FLOAT_MAT2 -> "GL_FLOAT_MAT2",
    GL_FLOAT_MAT3 -> "GL_FLOAT_MAT3",
    GL_FLOAT_MAT4 -> "GL_FLOAT_MAT4",
    GL_FLOAT_MAT2x3 -> "GL_FLOAT_MAT2x3",
    GL_FLOAT_MAT2x4 -> "GL_FLOAT_MAT2x4",
    GL_FLOAT_MAT3x2 -> "GL_FLOAT_MAT3x2",
    GL_FLOAT_MAT3x4 -> "GL_FLOAT_MAT3x4",
    GL_FLOAT_MAT4x2 -> "GL_FLOAT_MAT4x2",
    GL_FLOAT_MAT4x3 -> "GL_FLOAT_MAT4x3",
    GL_DOUBLE_MAT2 -> "GL_DOUBLE_MAT2",
    GL_DOUBLE_MAT3 -> "GL_DOUBLE_MAT3",
    GL_DOUBLE_MAT4 -> "GL_DOUBLE_MAT4",
    GL_DOUBLE_MAT2x3 -> "GL_DOUBLE_MAT2x3",
    GL_DOUBLE_MAT2x4 -> "GL_DOUBLE_MAT2x4",
    GL_DOUBLE_MAT3x2 -> "GL_DOUBLE_MAT3x2",
    GL_DOUBLE_MAT3x4 -> "GL_DOUBLE_MAT3x4",
    GL_DOUBLE_MAT4x2 -> "GL_DOUBLE_MAT4x2",
    GL_DOUBLE_MAT4x3 -> "GL_DOUBLE_MAT4x3",
    GL_UNSIGNED_INT_SAMPLER_2D -> "GL_UNSIGNED_INT_SAMPLER_2D")
}