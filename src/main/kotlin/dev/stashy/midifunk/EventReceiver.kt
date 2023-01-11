package dev.stashy.midifunk

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicInteger
import javax.sound.midi.MidiDevice
import javax.sound.midi.MidiMessage
import javax.sound.midi.MidiSystem
import javax.sound.midi.Receiver

val MidiDevice.Info.device get() = MidifunkDevice(MidiSystem.getMidiDevice(this))
fun MidiDevice.asMidifunk() = MidifunkDevice(this)

open class MidifunkDevice(val parent: MidiDevice) : MidiDevice by parent {
    val isInput get() = maxTransmitters > 0
    val isOutput get() = maxReceivers > 0

    internal val inputChannel = Channel<MidiEvent>()
    internal val outputChannel = Channel<MidiEvent>()

    internal val scope = CoroutineScope(Dispatchers.Default)

    var inputListeners = AtomicInteger(0)
    fun addInput() {
        val v = inputListeners.incrementAndGet()
    }

    fun removeInput() {
        val v = inputListeners.decrementAndGet()
        if (v <= 0) close()
    }

    val input = inputChannel.consumeAsFlow().shareIn(scope, SharingStarted.WhileSubscribed())
        .onStart { addInput() }
        .onCompletion { removeInput() }


    val inputReceiver = object : Receiver {
        override fun close() {
            inputChannel.close()
        }

        override fun send(message: MidiMessage, timeStamp: Long) = runBlocking {
            inputChannel.send(MidiEvent.convert(message.message.mapTo(mutableListOf()) { it.toUInt() }, timeStamp))
        }
    }

    init {
        transmitter.receiver = inputReceiver
        outputChannel.consumeAsFlow().onStart { open() }.onCompletion { close() }
            .onEach { receiver.send(it.convert(), it.timestamp) }.launchIn(scope)
    }

    override fun close() {
        scope.cancel()
        outputChannel.close()
        parent.close()
    }
}


