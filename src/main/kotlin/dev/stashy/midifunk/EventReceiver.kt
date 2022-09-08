package dev.stashy.midifunk

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import javax.sound.midi.MidiDevice
import javax.sound.midi.MidiMessage
import javax.sound.midi.Receiver

val MidiDevice.receive: SharedFlow<MidiEvent?>
    get() = eventReceiver.flow

internal val MidiDevice.eventReceiver: EventReceiver
    get() = (transmitter.receiver as? EventReceiver) ?: EventReceiver(this)

fun MidiDevice.send(e: MidiEvent) {
    receiver.send(e.convert(), e.timestamp)
}

class EventReceiver(private val dev: MidiDevice, setReceiver: Boolean = true, replay: Int = 100) :
    Receiver {

    internal val state: MutableSharedFlow<MidiEvent?> =
        MutableSharedFlow(replay = replay)

    val flow
        get() = state.asSharedFlow().onSubscription { dev.open() }

    init {
        if (setReceiver)
            dev.transmitter.receiver = this
    }

    override fun close() = runBlocking {
        state.emit(null)
    }

    override fun send(message: MidiMessage, timeStamp: Long) {
        MidiEvent.convert(message.message.mapTo(mutableListOf()) { it.toInt() }, timeStamp).let(state::tryEmit)
    }
}

fun Flow<MidiEvent?>.whileActive(): Flow<MidiEvent> =
    takeWhile { it != null }.filterNotNull()
