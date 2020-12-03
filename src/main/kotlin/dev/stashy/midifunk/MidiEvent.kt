package dev.stashy.midifunk

import javax.sound.midi.MidiMessage

open class MidiEvent(override var data: MutableList<Int>, override var timestamp: Long = -1) : MidiData {
    companion object {
        fun convert(data: MutableList<Int>, timestamp: Long = -1): MidiEvent {
            return when (data[0].msb) {
                MessageTypes.NoteOn, MessageTypes.NoteOff -> object : MidiEvent(data, timestamp),
                    NoteData {}
                MessageTypes.ControlChange -> object : MidiEvent(data, timestamp),
                    ControlData {}
                MessageTypes.Aftertouch -> object : MidiEvent(data, timestamp),
                    AftertouchData {}
                MessageTypes.ProgChange -> object : MidiEvent(data, timestamp),
                    ProgramData {}
                MessageTypes.ChanAftertouch -> object : MidiEvent(data, timestamp),
                    AftertouchData {}
                MessageTypes.PitchRange -> object : MidiEvent(data, timestamp),
                    PitchWheelRangeData {}
                MessageTypes.SysEx -> object : MidiEvent(data, timestamp),
                    SysExData {}
                else -> return object : MidiEvent(data, timestamp) {}
            }
        }
    }

    fun convert(): MidiMessage {
        return object : MidiMessage(data.map { it.toByte() }.toByteArray()) {
            override fun clone(): Any {
                return this
            }
        }
    }
}

interface MidiData {
    var data: MutableList<Int>
    var timestamp: Long
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

interface NoteData : MidiData,
    ChannelData {
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

interface AftertouchData : MessageData,
    MidiData, NoteData {
    var pressure: Int
        get() = if (message == MessageTypes.ChanAftertouch) data[2] else data[1]
        set(value) {
            if (message == MessageTypes.ChanAftertouch)
                data[2] = value
            else
                data[1] = value
        }
}

interface ProgramData : MidiData,
    ChannelData {
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
    var sysEx: List<Int>
        get() = data.subList(1, data.size - 1)
        set(value) {
            val l = value.toMutableList()
            l.add(0, data[0])
            data = l
        }
}