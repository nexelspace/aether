package space.nexel.aether.core.sys

trait EventQueue {
  def add(event: Event): Unit 
}
