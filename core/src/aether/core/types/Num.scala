package aether.core.types

enum Num(val bytes: Int, val float: Boolean, val signed: Boolean) {
  case Byte extends Num(1, false, true)
  case UByte extends Num(1, false, false)
  case Short extends Num(2, false, true)
  case UShort extends Num(2, false, false)
  case Int extends Num(4, false, true)
  case UInt extends Num(4, false, false)
  case HalfFloat extends Num(2, false, true)
  case Float extends Num(4, true, true)
  case Double extends Num(8, true, true)
}
