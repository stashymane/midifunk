package dev.stashy.midifunk

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.sound.midi.MidiDevice
import javax.sound.midi.MidiMessage
import javax.sound.midi.Receiver

val MidiDevice.from: SharedFlow<MidiEvent?>
    get() {
        (this.transmitter.receiver as? EventReceiver)?.let { return it.flow }
        return EventReceiver(this).flow
    }

fun MidiDevice.to(e: MidiEvent) {
    receiver.send(e.convert(), e.timestamp)
}

class EventReceiver(private val dev: MidiDevice, setReceiver: Boolean = true) :
    Receiver {

    val flow = MutableStateFlow<MidiEvent?>(null)

    init {
        if (setReceiver)
            dev.transmitter.receiver = this
    }

    override fun close() {
        flow.tryEmit(null)
    }

    override fun send(message: MidiMessage, timeStamp: Long) {
        MidiEvent.convert(message.message.mapTo(mutableListOf()) { it.toInt() }, timeStamp).let(flow::tryEmit)
    }
}

