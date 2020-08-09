package dev.stashy.midibind.midi

import dev.stashy.midibind.midi.definitions.MidiEvent
import javax.sound.midi.MidiDevice
import javax.sound.midi.MidiMessage
import javax.sound.midi.MidiSystem
import javax.sound.midi.Receiver

class Device(val dev: MidiDevice) : Receiver {
    constructor(info: MidiDevice.Info) : this(MidiSystem.getMidiDevice(info))

    val receivers = mutableListOf<EventReceiver>()

    init {
        dev.open()
        dev.transmitter.receiver = this
    }

    override fun send(message: MidiMessage?, timeStamp: Long) {
        if (message == null) return
        val data = MidiEvent.convert(message.message.map { it.toInt() }.toTypedArray())
        receivers.forEach { it.sendMessage(data) }
    }

    override fun close() {
        dev.close()
    }
}