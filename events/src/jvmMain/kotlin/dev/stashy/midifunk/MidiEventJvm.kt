package dev.stashy.midifunk

import javax.sound.midi.MidiMessage

fun MidiMessage.data() = message.mapTo(mutableListOf()) { it.toUInt() }

/**
 * Converts a Midifunk MidiEvent into a Java MidiMessage.
 * @see MidiMessage
 */
fun MidiData.toJvmMessage(): MidiMessage {
    return object : MidiMessage(data.map(UInt::toByte).toByteArray()) {
        override fun clone(): Any {
            return this
        }
    }
}
