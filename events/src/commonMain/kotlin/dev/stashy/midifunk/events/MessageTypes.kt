package dev.stashy.midifunk.events

object MessageTypes {
    const val NoteOn: UInt = 9u
    const val NoteOff: UInt = 8u
    const val Pressure: UInt = 10u
    const val ControlChange: UInt = 11u
    const val ProgChange: UInt = 12u
    const val ChannelPressure: UInt = 13u
    const val PitchBend: UInt = 14u
    const val SysEx: UInt = 15u
}
