package dev.stashy.midibind.midi.definitions

import dev.stashy.midibind.midi.MessageTypes
import dev.stashy.midibind.midi.lsb
import dev.stashy.midibind.midi.msb

open class MidiEvent(override val data: Array<Int>) : MidiData {
    companion object {
        fun convert(data: Array<Int>): MidiEvent {
            return when (data[0].msb) {
                MessageTypes.NoteOn, MessageTypes.NoteOff -> object : MidiEvent(data), NoteData,
                    VelocityData {}
                MessageTypes.ControlChange -> object : MidiEvent(data), ControlData {}
                MessageTypes.Aftertouch -> object : MidiEvent(data), AftertouchData {}
                MessageTypes.ProgChange -> object : MidiEvent(data), ProgramData {}
                MessageTypes.ChanAftertouch -> object : MidiEvent(data), AftertouchData {}
                MessageTypes.PitchRange -> object : MidiEvent(data), PitchWheelRangeData {}
                MessageTypes.SysEx -> object : MidiEvent(data), SysExData {}
                else -> return object : MidiEvent(data) {}
            }
        }
    }
}

interface MidiData {
    val data: Array<Int>
}

interface StatusData : MidiData {
    val status: Int
        get() = data[0]
}

interface MessageData : StatusData {
    val message: Int
        get() = status.msb
}

interface ChannelData : StatusData {
    val channel: Int
        get() = status.lsb
}

interface NoteData : MidiData, ChannelData {
    val note: Int
        get() = data[1]
    val noteStatus: Boolean
        get() = data[0].msb == MessageTypes.NoteOn
}

interface VelocityData : MidiData, NoteData {
    val velocity: Int
        get() = data[2]
}

interface ControlData : ChannelData {
    val control: Int
        get() = data[1]
    val value: Int
        get() = data[2]
}

interface AftertouchData : MessageData, MidiData, NoteData {
    val pressure: Int
        get() = if (message == MessageTypes.ChanAftertouch) data[2] else data[1]
}

interface ProgramData : MidiData, ChannelData {
    val program: Int
        get() = data[1]
}

interface PitchWheelRangeData : MidiData { //TODO check how this works
    val min: Int
        get() = data[1].lsb
    val max: Int
        get() = data[2].msb
}

interface SysExData : MidiData {
    val sysEx: Array<Int>
        get() = data.copyOfRange(1, data.size - 2)
}