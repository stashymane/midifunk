package dev.stashy.midibind.midi.definitions

object NoteEvents {
    class On(override val data: Array<Int>) : StatusData, ChannelData, NoteData, VelocityData {}
    class Off(override val data: Array<Int>) : StatusData, ChannelData, NoteData, VelocityData {}

    class Aftertouch(override val data: Array<Int>) : StatusData, ChannelData, NoteData, AftertouchData {}
    class ControlChange(override val data: Array<Int>) : StatusData, ChannelData {}
}