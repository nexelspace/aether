package space.nexel.aether.core.input

import space.nexel.aether.core.platform.Event

object MidiEvent {
  // http://www.songstuff.com/recording/article/midi_message_format/
  object Status {
    val NoteOff = 0x80
    val NoteOn = 0x90
    val PolyphonicAftertouch = 0xa0
    val ControlChange = 0xb0
    val ProgramChange = 0xc0
    val ChannelAftertouch = 0xd0
    val PitchWheel = 0xe0
  }
}

case class MidiEvent(status: Int, data1: Int, data2: Int) extends Event
