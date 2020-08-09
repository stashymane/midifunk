package dev.stashy.midifunk

object MessageTypes {
    const val NoteOn = 0x9
    const val NoteOff = 0x8
    const val Aftertouch = 0xA
    const val ControlChange = 0xB
    const val ProgChange = 0xC
    const val ChanAftertouch = 0xD
    const val PitchRange = 0xE
    const val SysEx = 0xF
}