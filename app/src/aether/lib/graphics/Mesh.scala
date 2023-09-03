package aether.lib.graphics

import aether.core.graphics.ShaderBuffer.Flag
import aether.core.graphics.ShaderBuffer.Size
import aether.core.graphics.ShaderBuffer.Target
import aether.core.graphics.ShaderBuffer.Type
import aether.core.graphics.ShaderProgram

import scala.collection.mutable
import aether.core.graphics.Graphics

object Mesh {

  private var factory_ : Factory = null

  def factory(using Graphics) = {
    if (factory_ == null) factory_ = new Factory
    factory_
  }

  class Factory(using Graphics) {
    def staticPCN(size: Int) = new MeshPCN(float(size), colors(size), float(size))
    def staticPT(size: Int) = new MeshPT(float(size), tex(size))
    def staticP(size: Int) = new Mesh(float(size))

    def dynamicP() = new Mesh(float())
    def dynamicPC() = new MeshPC(float(), float())
    def dynamicPT() = new MeshPT(float(), tex())
    def dynamicPCN() = new MeshPCN(float(), colors(), float())
    def dynamicPNT() = new MeshPNT(float(), float(), tex())
    def dynamicPCNT() = new MeshPCNT(float(), colors(), float(), tex())

    def dynamicPIT() = new Mesh(float(), indices(), null, null, tex())
    def dynamicPINT() = new Mesh(float(), indices(), null, float(), tex())
    def dynamicPINCT() = new Mesh(float(), indices(), colors(), float(), tex())

    def dynamic(flags: String) = new Mesh(float(),
      if (flags.contains("I")) indices() else null,
      if (flags.contains("C")) colors() else null,
      if (flags.contains("N")) float() else null,
      if (flags.contains("T")) tex() else null)

    private def tex(size: Int) = ShaderVarBuffer(Type.Float | Size.Static | Target.Vertex, size, 2)
    private def float(size: Int) = ShaderVarBuffer(Type.Float | Size.Static | Target.Vertex, size, 3)
    private def colors(size: Int) = ShaderVarBuffer(Type.UByte | Size.Static | Target.Vertex | Flag.Normalize, size, 3)

    private def float() = ShaderVarBuffer(Type.Float | Size.Dynamic | Target.Vertex, 0, 3)
    private def indices() = ShaderVarBuffer(Type.UInt | Size.Dynamic | Target.Index, 0, 1)
    private def colors() = ShaderVarBuffer(Type.UByte | Size.Dynamic | Target.Vertex | Flag.Normalize, 0, 4)
    private def tex() = ShaderVarBuffer(Type.Float | Size.Dynamic | Target.Vertex, 0, 2)
  }

}

class Mesh(val positions: ShaderVarBuffer,
           val indices: ShaderVarBuffer = null,
           val colors: ShaderVarBuffer = null,
           val normals: ShaderVarBuffer = null,
           val texCoords: ShaderVarBuffer = null) {

  def hasIndices = indices != null

  def hasColors = colors != null

  def hasNormals = normals != null

  def hasTexCoords = texCoords != null

  override def toString = {
    val opts = mutable.Buffer[(String, Int)]()
    opts += "P" → positions.size
    if (hasIndices) opts += "I" → indices.size
    if (hasColors) opts += "C" → colors.size
    if (hasNormals) opts += "N" → normals.size
    if (hasTexCoords) opts += "T" → texCoords.size
    val name = opts.map(_._1).mkString
    val sizes = opts.map(_._2).mkString(",")
    s"Mesh-$name[$sizes]"
  }
  def size = {
    positions.size
  }

  def clear(): Unit = {
    positions.clear()
    if (hasIndices) indices.clear()
    if (hasColors) colors.clear()
    if (hasNormals) normals.clear()
    if (hasTexCoords) texCoords.clear()
  }

  def assertComplete(): Unit = {
    val pos = positions.elementPos
    assert(!hasColors || colors.elementPos == pos, this)
    assert(!hasNormals || normals.elementPos == pos, this)
    assert(!hasTexCoords || texCoords.elementPos == pos, this)
  }

  val aPosition = "a_position"
  val aColor = "a_color"
  val aNormal = "a_normal"
  val aTexCoord = "a_texCoord"

  def toProgram(program: ShaderProgram) = {

    if (program.hasAttribute(aPosition)) {
      program.attributeBuffer(aPosition, positions.buffer, positions.numComponents)
    }
    //    program.attributeBuffer("a_color", colors)
    if (program.hasAttribute(aColor)) {
      if (hasColors) {
        program.attributeBuffer(aColor, colors.buffer, colors.numComponents)
      } else {
        program.attribute(aColor).get.put4F(1, 1, 1, 1)
      }
    }
    if (program.hasAttribute(aNormal)) {
      if (hasNormals) {
        program.attributeBuffer(aNormal, normals.buffer, normals.numComponents)
      } else {
        program.attribute(aNormal).get.put4F(1, 1, 1, 1)
      }
    }
    if (program.hasAttribute(aTexCoord)) {
      if (hasTexCoords) {
        program.attributeBuffer(aTexCoord, texCoords.buffer, texCoords.numComponents)
      } else {
        program.attribute(aTexCoord).get.put2F(0, 0)
      }
    }
  }
}

class MeshPCN(positions: ShaderVarBuffer,
              colors: ShaderVarBuffer,
              normals: ShaderVarBuffer)
  extends Mesh(positions, null, colors, normals)

class MeshPT(positions: ShaderVarBuffer, texCoords: ShaderVarBuffer)
  extends Mesh(positions, null, null, null, texCoords)

class MeshPC(positions: ShaderVarBuffer, colors: ShaderVarBuffer)
  extends Mesh(positions, null, colors)

class MeshPNT(positions: ShaderVarBuffer, normals: ShaderVarBuffer, texCoords: ShaderVarBuffer)
  extends Mesh(positions, null, null, normals, texCoords)

class MeshPCNT(positions: ShaderVarBuffer, colors: ShaderVarBuffer, normals: ShaderVarBuffer, texCoords: ShaderVarBuffer)
  extends Mesh(positions, null, colors, normals, texCoords)
