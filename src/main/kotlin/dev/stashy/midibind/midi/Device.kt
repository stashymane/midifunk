package dev.stashy.midibind.midi

import dev.stashy.midibind.midi.definitions.MidiEvent
import javax.sound.midi.MidiDevice
import javax.sound.midi.MidiMessage
import javax.sound.midi.MidiSystem
import javax.sound.midi.Receiver

class Device(private val dev: MidiDevice) : Receiver {
    constructor(info: MidiDevice.Info) : this(MidiSystem.getMidiDevice(info))

    val executors = mutableListOf<EventReceiver>()

    init {
        dev.transmitters.forEach { //TODO automatically open/close channels based on actions
            it.receiver = this
        }
        dev.transmitter.receiver = this
        dev.open()
    }

    override fun send(message: MidiMessage?, timeStamp: Long) {
        if (message == null) return
        val data = MidiEvent.convert(message.message.map { it.toInt() }.toTypedArray())
        executors.forEach { it.sendMessage(data) }
    }

    override fun close() {
        dev.close()
    }
}