package dev.stashy.midibind.midi.definitions

import dev.stashy.midibind.midi.*

open class MidiEvent(override var data: Array<Int>) : MidiData {
    companion object {
        fun convert(data: Array<Int>): MidiEvent {
            return when (data[0].msb) {
                MessageTypes.NoteOn, MessageTypes.NoteOff -> object : MidiEvent(data), NoteData {}
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
    var data: Array<Int>
}

interface StatusData : MidiData {
    var status: Int
        get() = data[0]
        set(value) {
            data[0] = value
        }
}

interface MessageData : StatusData {
    var message: Int
        get() = status.msb
        set(value) {
            status = status.withMsb(value)
        }
}

interface ChannelData : StatusData {
    var channel: Int
        get() = status.lsb
        set(value) {
            status = status.withLsb(value)
        }
}

interface NoteData : MidiData, ChannelData {
    var noteStatus: Boolean
        get() = data[0].msb == MessageTypes.NoteOn
        set(value) {
            data[0] = data[0].withMsb(if (value) MessageTypes.NoteOn else MessageTypes.NoteOff)
        }
    var note: Int
        get() = data[1]
        set(value) {
            data[1] = value
        }
    var velocity: Int
        get() = data[2]
        set(value) {
            data[2] = value
        }
}

interface ControlData : ChannelData {
    var control: Int
        get() = data[1]
        set(value) {
            data[1] = value
        }
    var value: Int
        get() = data[2]
        set(value) {
            data[2] = value
        }
}

interface AftertouchData : MessageData, MidiData, NoteData {
    var pressure: Int
        get() = if (message == MessageTypes.ChanAftertouch) data[2] else data[1]
        set(value) {
            if (message == MessageTypes.ChanAftertouch)
                data[2] = value
            else
                data[1] = value
        }
}

interface ProgramData : MidiData, ChannelData {
    var program: Int
        get() = data[1]
        set(value) {
            data[1] = value
        }
}

interface PitchWheelRangeData : MidiData { //TODO check how this works
    var min: Int
        get() = data[1].lsb
        set(value) {
            data[1] = data[1].withLsb(value)
        }
    var max: Int
        get() = data[2].msb
        set(value) {
            data[2] = data[2].withMsb(value)
        }
}

interface SysExData : MidiData {
    var sysEx: Array<Int>
        get() = data.copyOfRange(1, data.size - 2)
        set(value) {
            data = arrayOf(data[0]).plus(value.copyOfRange(0, value.size - 1))
        }
}