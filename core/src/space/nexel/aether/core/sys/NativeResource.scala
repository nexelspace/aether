package space.nexel.aether.core.sys

trait NativeResource[T,C <: Resource.Config](using factory: Resource.Factory[T, C]) {

  def release(): Unit
  
}
