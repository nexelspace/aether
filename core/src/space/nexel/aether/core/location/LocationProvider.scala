package space.nexel.aether.core.location

import space.nexel.aether.core.platform.Event

object LocationProvider {
  case class LocationEvent(location: Location, accuracy: Float, time: Time) extends Event {

  }
}

abstract class LocationProvider {
  // def listen()
}
