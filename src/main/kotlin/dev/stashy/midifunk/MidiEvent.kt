package dev.stashy.midifunk

import javax.sound.midi.MidiMessage


interface MidiEventCompanion<T> {
    /**
     * Creates a MidiEvent from a list of unsigned integers.
     */
    fun toEvent(data: List<UInt>, timestamp: Long = 0): T

    /**
     * MidiEvent DSL builder.
     */
    fun create(timestamp: Long = 0, fn: T.() -> Unit): T
}

/**
 * MIDI event data class. Use the `convert()` function in the companion object to turn MIDI data into a type-safe object.
 * Otherwise, use `convert()` on a MidiData object itself to convert it back to a MidiMessage.
 * To get event-specific data, type check & cast to different MidiData interfaces.
 * @see MidiData
 * @see MidiEvent.Companion.convert
 * @see MidiData.toMessage
 */

open class MidiEvent(override var data: MutableList<UInt>, override var timestamp: Long = -1) : MidiData {
    companion object {
        fun convert(message: MidiMessage, timestamp: Long = -1): MidiData =
            convert(message.message.mapTo(mutableListOf()) { it.toUInt() }, timestamp)

        fun convert(data: MutableList<UInt>, timestamp: Long = -1): MidiData {
            return when (data[0].msb) {
                MessageTypes.NoteOn, MessageTypes.NoteOff -> NoteEvent.create(data, timestamp)
                MessageTypes.ControlChange -> ControlEvent.create(data, timestamp)
                MessageTypes.Pressure -> PressureEvent.create(data, timestamp)
                MessageTypes.ChannelPressure -> ChannelPressureEvent.create(data, timestamp)
                MessageTypes.ProgChange -> ProgramEvent.create(data, timestamp)
                MessageTypes.PitchBend -> PitchBendEvent.create(data, timestamp)
                MessageTypes.SysEx -> SysExEvent.create(data, timestamp)
                else -> return object : MidiEvent(data, timestamp) {}
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        return other is MidiEvent && other.data == data && other.timestamp == timestamp
    }

    override fun hashCode(): Int {
        var result = data.hashCode()
        result = 31 * result + timestamp.hashCode()
        return result
    }
}

/**
 * Base MIDI data type - contains data that is shared by all events.
 * Additional values can be retrieved or modified with other event or data interfaces.
 */
interface MidiData {
    /**
     * Raw MIDI bytes represented in a list of unsigned integers.
     */
    var data: MutableList<UInt>

    var timestamp: Long

    /**
     * The status byte of an event's MIDI data.
     * Not recommended to change directly.
     */
    var status: UInt
        get() = data[0]
        set(value) {
            data[0] = value
        }

    /**
     * The type of MIDI event, corresponding to the ones defined in the MessageTypes object.
     * @see MessageTypes
     */
    var type: UInt
        get() = status.msb
        set(value) {
            status = status.withMsb(value)
        }

    /**
     * Converts a Midifunk MidiEvent into a Java MidiMessage.
     * @see MidiMessage
     */
    fun toMessage(): MidiMessage {
        return object : MidiMessage(data.map(UInt::toByte).toByteArray()) {
            override fun clone(): Any {
                return this
            }
        }
    }

    companion object {
        internal fun validate(name: String, data: List<UInt>, size: Int, status: UInt) {
            if (data.size < size)
                throw MidiDataException.size(name, size, data.size)
            data.first().msb.let {
                if (it != status)
                    throw MidiDataException.status(name, status, it)
            }
        }
    }
}

interface ChannelData : MidiData {
    /**
     * The channel of a MIDI event.
     * Channel number must be in the range of 0 to 15.
     */
    var channel: UInt
        get() = status.lsb
        set(value) {
            status = status.withLsb(value)
        }
}

interface NoteData : MidiData {
    /**
     * The note of a MIDI event.
     * Note number must be in the range of 0 to 127.
     * @see Note
     */
    var note: UInt
        get() = data[1]
        set(value) {
            data[1] = value and 127u
        }
}

/**
 * A MIDI note on/off event.
 */
interface NoteEvent : MidiData, NoteData,
    ChannelData {
    /**
     * Whether the note event is ON (true) or OFF (false).
     */
    var noteStatus: Boolean
        get() = data[0].msb == MessageTypes.NoteOn
        set(value) {
            data[0] = data[0].withMsb(if (value) MessageTypes.NoteOn else MessageTypes.NoteOff)
        }

    /**
     * The velocity of a note event.
     * Value must be in the range of 0 to 127.
     */
    var velocity: UInt
        get() = data[2]
        set(value) {
            data[2] = value and 127u
        }

    companion object : MidiEventCompanion<NoteEvent> {
        override fun toEvent(data: List<UInt>, timestamp: Long): NoteEvent {
            if (data.size < 3)
                throw MidiDataException.size("note", 3, data.size)
            data.first().msb.let {
                if (it != MessageTypes.NoteOn && it != MessageTypes.NoteOff)
                    throw MidiDataException.status("note", listOf(MessageTypes.NoteOn, MessageTypes.NoteOn), it)
            }
            return create(data, timestamp)
        }

        override fun create(timestamp: Long, fn: NoteEvent.() -> Unit): NoteEvent =
            object : MidiEvent(
                mutableListOf(0u.withMsb(MessageTypes.NoteOn), 0u, 0u),
                timestamp
            ), NoteEvent {}.apply { fn.invoke(this) }

        internal fun create(data: List<UInt>, timestamp: Long = 0): NoteEvent =
            object : MidiEvent(data.toMutableList(), timestamp), NoteEvent {}
    }
}

/**
 * MIDI control change (CC) event.
 */
interface ControlEvent : ChannelData {
    /**
     * Which control or knob is affected by an event.
     */
    var control: UInt
        get() = data[1]
        set(value) {
            data[1] = value
        }

    /**
     * The value of a control or knob.
     */
    var value: UInt
        get() = data[2]
        set(value) {
            data[2] = value
        }

    companion object : MidiEventCompanion<ControlEvent> {
        override fun toEvent(data: List<UInt>, timestamp: Long): ControlEvent {
            MidiData.validate("control change", data, 3, MessageTypes.ControlChange)
            return create(data, timestamp)
        }

        override fun create(timestamp: Long, fn: ControlEvent.() -> Unit): ControlEvent =
            object : MidiEvent(
                mutableListOf(0u.withMsb(MessageTypes.ControlChange), 0u, 0u),
                timestamp
            ), ControlEvent {}.apply { fn.invoke(this) }

        internal fun create(data: List<UInt>, timestamp: Long = 0): ControlEvent =
            object : MidiEvent(data.toMutableList(), timestamp), ControlEvent {}
    }
}

/**
 * MIDI polyphonic after-touch event.
 */
interface PressureEvent : MidiData, NoteData, ChannelData {
    /**
     * The pressure of a given note.
     * Value must be in the range of 0 to 127.
     */
    var pressure: UInt
        get() = data[2]
        set(value) {
            data[2] = value and 127u
        }

    companion object : MidiEventCompanion<PressureEvent> {
        override fun toEvent(data: List<UInt>, timestamp: Long): PressureEvent {
            MidiData.validate("pressure", data, 3, MessageTypes.Pressure)
            return create(data, timestamp)
        }

        override fun create(timestamp: Long, fn: PressureEvent.() -> Unit): PressureEvent =
            object : MidiEvent(
                mutableListOf(0u.withMsb(MessageTypes.Pressure), 0u, 0u),
                timestamp
            ), PressureEvent {}.apply { fn.invoke(this) }

        internal fun create(data: List<UInt>, timestamp: Long = 0): PressureEvent =
            object : MidiEvent(data.toMutableList(), timestamp), PressureEvent {}
    }
}

/**
 * MIDI channel after-touch event.
 */
interface ChannelPressureEvent : MidiData, ChannelData {
    /**
     * The pressure of a given channel.
     * Value must be in the range of 0 to 127.
     */
    var pressure: UInt
        get() = data[1]
        set(value) {
            data[1] = value and 127u
        }

    companion object : MidiEventCompanion<ChannelPressureEvent> {
        override fun toEvent(data: List<UInt>, timestamp: Long): ChannelPressureEvent {
            MidiData.validate("channel pressure", data, 2, MessageTypes.ChannelPressure)
            return create(data, timestamp)
        }

        override fun create(timestamp: Long, fn: ChannelPressureEvent.() -> Unit): ChannelPressureEvent =
            object : MidiEvent(
                mutableListOf(0u.withMsb(MessageTypes.ChannelPressure), 0u, 0u),
                timestamp
            ), ChannelPressureEvent {}.apply { fn.invoke(this) }

        internal fun create(data: List<UInt>, timestamp: Long = 0): ChannelPressureEvent =
            object : MidiEvent(data.toMutableList(), timestamp), ChannelPressureEvent {}
    }
}

/**
 * MIDI program change event.
 */
interface ProgramEvent : MidiData,
    ChannelData {
    /**
     * The program this event refers to.
     * Value must be in the range of 0 to 127.
     */
    var program: UInt
        get() = data[1]
        set(value) {
            data[1] = value and 127u
        }

    companion object : MidiEventCompanion<ProgramEvent> {
        override fun toEvent(data: List<UInt>, timestamp: Long): ProgramEvent {
            MidiData.validate("program change", data, 2, MessageTypes.ProgChange)
            return create(data, timestamp)
        }

        override fun create(timestamp: Long, fn: ProgramEvent.() -> Unit): ProgramEvent =
            object : MidiEvent(
                mutableListOf(0u.withMsb(MessageTypes.ProgChange), 0u, 0u),
                timestamp
            ), ProgramEvent {}.apply { fn.invoke(this) }

        internal fun create(data: List<UInt>, timestamp: Long = 0): ProgramEvent =
            object : MidiEvent(data.toMutableList(), timestamp), ProgramEvent {}
    }
}

/**
 * MIDI pitch bend event.
 */
interface PitchBendEvent : MidiData {
    /**
     * The value of the pitch bend.
     * @see MAX_VALUE
     * @see CENTER
     */
    var value: UInt
        get() = data[2] shl 7 or data[1]
        set(value) {
            data[1] = value and 127u
            data[2] = value shr 7 and 127u
        }

    /**
     * The value of the pitch bend, represented as a range between -1.0 and 1.0.
     *
     * Warning: this value is automatically mapped to and from MIDI data bytes,
     *  therefore your value is not guaranteed to be the exact same when getting if you set it to an arbitrary value (e.g. 0.2).
     *
     * If you need exact precision, consider using the `value` variable.
     * @see value
     */
    var range: Float
        get() {
            val normalized = value.toInt() / MAX_VALUE.toFloat()
            return (normalized * 2f) - 1f
        }
        set(value) {
            if (value == 0.0f) {
                this.value = CENTER
                return
            }
            val normalized = (value.coerceIn(-1.0f, 1.0f) + 1) / 2.0
            this.value = (normalized * MAX_VALUE.toInt()).toUInt().coerceIn(0u, MAX_VALUE)
        }

    companion object : MidiEventCompanion<PitchBendEvent> {
        /**
         * The largest value a pitch bend event can send.
         */
        const val MAX_VALUE: UInt = 16383u

        /**
         * The value at which the pitch bend is exactly zero.
         */
        const val CENTER: UInt = 8192u

        override fun toEvent(data: List<UInt>, timestamp: Long): PitchBendEvent {
            MidiData.validate("pitch bend", data, 2, MessageTypes.PitchBend)
            return create(data, timestamp)
        }

        override fun create(timestamp: Long, fn: PitchBendEvent.() -> Unit): PitchBendEvent =
            object : MidiEvent(
                mutableListOf(0u.withMsb(MessageTypes.PitchBend), 0u, 64u),
                timestamp
            ), PitchBendEvent {}.apply { fn.invoke(this) }

        internal fun create(data: List<UInt>, timestamp: Long = 0): PitchBendEvent =
            object : MidiEvent(data.toMutableList(), timestamp), PitchBendEvent {}
    }
}

/**
 * MIDI system exclusive event.
 */
interface SysExEvent : MidiData {
    /**
     * All system exclusive bytes, excluding the initial byte (0xF & SysEx type).
     */
    var sysEx: List<UInt>
        get() = data.subList(1, data.size - 1)
        set(value) {
            val l = value.toMutableList()
            l.add(0, data[0])
            data = l
        }

    /**
     * The type of SysEx event, defined in the Type enum.
     * @see SysExEvent.Type
     */
    var sysExType: Type
        get() {
            val i = data[0].toInt() and 0xFF - 240;
            return if (i <= Type.values().size)
                Type.values()[i]
            else
                Type.Unknown
        }
        set(value) {
            data[0] = value.code
        }

    companion object : MidiEventCompanion<SysExEvent> {
        override fun toEvent(data: List<UInt>, timestamp: Long): SysExEvent {
            MidiData.validate("system exclusive", data, 1, MessageTypes.SysEx)
            return create(data, timestamp)
        }

        override fun create(timestamp: Long, fn: SysExEvent.() -> Unit): SysExEvent =
            object : MidiEvent(
                mutableListOf(0u.withMsb(MessageTypes.SysEx), 0u, 0u),
                timestamp
            ), SysExEvent {}.apply { fn.invoke(this) }

        internal fun create(data: List<UInt>, timestamp: Long = 0): SysExEvent =
            object : MidiEvent(data.toMutableList(), timestamp), SysExEvent {}
    }

    /**
     * All SysEx types defined in the specification.
     */
    enum class Type(val code: UInt) {
        Unknown(240u),
        TimeCode(241u),
        SongPosition(242u),
        SongSelect(243u),
        Reserved1(244u),
        Reserved2(245u),
        TuneRequest(246u),
        EndMessage(247u),
        TimingClock(248u),
        Reserved3(249u),
        Start(250u),
        Continue(251u),
        Stop(252u),
        Reserved4(253u),
        ActiveSensing(254u),
        SystemReset(255u)
    }
}

class MidiDataException(message: String) : Exception(message) {
    companion object {
        fun status(name: String, expected: List<UInt>, actual: UInt) =
            MidiDataException("Invalid MIDI $name event status: expected ${expected.joinToString { "," }}, received $actual")

        fun status(name: String, expected: UInt, actual: UInt) = status(name, listOf(expected), actual)

        fun size(name: String, expected: Int, actual: Int) =
            MidiDataException("Invalid MIDI $name event: expected $expected bytes, received $actual")
    }
}
