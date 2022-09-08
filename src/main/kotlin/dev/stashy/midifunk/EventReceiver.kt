package dev.stashy.midifunk

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import javax.sound.midi.MidiDevice
import javax.sound.midi.MidiMessage
import javax.sound.midi.Receiver

val MidiDevice.input: Flow<MidiEvent>
    get() {
        val receiver = (transmitter.receiver as? EventReceiver) ?: EventReceiver(this@input)
        return receiver.flow
    }

fun MidiDevice.output(e: MidiEvent) {
    receiver.send(e.convert(), e.timestamp)
}

class EventReceiver(private val dev: MidiDevice, setReceiver: Boolean = true) :
    Receiver {

    private val state = MutableStateFlow<MidiEvent?>(null)
    private val shared = state.asSharedFlow()
    val flow
        get() = shared.onEach { if (it == null) throw CancellationException("Device ${dev.deviceInfo.name} has been closed.") }
            .filterNotNull()

    init {
        if (setReceiver)
            dev.transmitter.receiver = this
    }

    override fun close() {
        state.tryEmit(null)
    }

    override fun send(message: MidiMessage, timeStamp: Long) {
        MidiEvent.convert(message.message.mapTo(mutableListOf()) { it.toInt() }, timeStamp).let(state::tryEmit)
    }
}

