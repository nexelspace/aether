package aether.core.platform

trait NativeResource[T,C <: Resource.Config](using factory: Resource.Factory[T, C]) {

  def release(): Unit
  
}
