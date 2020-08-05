package dev.stashy.midibind.midi

import dev.stashy.midibind.midi.definitions.MidiEvent
import javax.sound.midi.MidiDevice
import javax.sound.midi.MidiMessage
import javax.sound.midi.Receiver

class Device(val info: MidiDevice.Info) : Receiver {
    val actions = mutableListOf<() -> Unit>()

    override fun send(message: MidiMessage?, timeStamp: Long) {
        if (message == null) return
        var data = MidiEvent.convert(message.message.map { it.toInt() }.toTypedArray())
    }

    override fun close() {

    }
}