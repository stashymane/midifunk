package dev.stashy.midifunk.device

import dev.stashy.midifunk.MidiData
import dev.stashy.midifunk.MidiEvent
import dev.stashy.midifunk.isInput
import dev.stashy.midifunk.isOutput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import javax.sound.midi.MidiDevice
import javax.sound.midi.MidiMessage
import javax.sound.midi.MidiSystem
import javax.sound.midi.Receiver

val MidiDevice.Info.device get() = MidiSystem.getMidiDevice(this).asMidifunk()
fun MidiDevice.asMidifunk() = MidifunkDevice.create(this)

interface InputDevice {
    val input: InputPort
}

interface OutputDevice {
    val output: OutputPort
}

interface MidifunkDevice {
    fun close()

    companion object {
        fun create(device: MidiDevice): MidifunkDevice {
            val isInput = device.isInput
            val isOutput = device.isOutput

            return when {
                isInput && isOutput -> object : MidifunkDevice, InputDevice by genericInput(device),
                    OutputDevice by genericOutput(device) {
                    override fun close() {
                        input.close()
                        output.close()
                    }
                }

                isInput && !isOutput -> object : MidifunkDevice, InputDevice by genericInput(device) {
                    override fun close() = input.close()
                }

                !isInput && isOutput -> object : MidifunkDevice, OutputDevice by genericOutput(device) {
                    override fun close() = output.close()
                }

                else -> object : MidifunkDevice {
                    override fun close() = Unit
                }
            }
        }

        fun genericInput(device: MidiDevice): InputDevice = object : InputDevice {
            override val input: InputPort = object : InputPort {
                val scope = CoroutineScope(Dispatchers.Default)

                val channel = Channel<MidiData>()
                val receiver = channelReceiver(channel)
                val flow = channel.consumeAsFlow().shareIn(scope, SharingStarted.WhileSubscribed())

                override fun open(): Flow<MidiData> {
                    device.transmitter.receiver = receiver
                    device.open()
                    return flow
                }

                override fun close() {
                    channel.close()
                }
            }
        }

        fun genericOutput(device: MidiDevice): OutputDevice = object : OutputDevice {
            override val output: OutputPort = object : OutputPort {
                val scope = CoroutineScope(Dispatchers.Default)

                val channel = Channel<MidiData>()

                override fun open(): SendChannel<MidiData> {
                    device.open()
                    channel.consumeAsFlow().onEach { device.receiver.send(it.toMessage(), it.timestamp) }
                        .launchIn(scope)
                    return channel
                }

                override fun close() {
                    channel.close()
                }
            }
        }


        fun channelReceiver(channel: Channel<MidiData>) = object : Receiver {
            override fun close() {
                channel.close()
            }

            override fun send(message: MidiMessage, timeStamp: Long) = runBlocking {
                channel.send(MidiEvent.convert(message, timeStamp))
            }
        }
    }
}


