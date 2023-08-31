package space.nexel.aether.server

import pureconfig.ConfigReader
import pureconfig.generic.derivation.ConfigReaderDerivation.Default.derived

case class Config(interface: String, port: Int) derives ConfigReader

