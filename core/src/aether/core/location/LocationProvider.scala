package aether.core.location

import aether.core.platform.Event
import aether.core.types.Location
import aether.core.types.Time

object LocationProvider {
  case class LocationEvent(location: Location, accuracy: Float, time: Time) extends Event {

  }
}

abstract class LocationProvider {
  // def listen()
}
