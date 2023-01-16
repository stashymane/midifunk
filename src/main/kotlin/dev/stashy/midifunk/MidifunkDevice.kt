package dev.stashy.midifunk

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

interface MidiPort {
    fun close()
}

interface InputPort : MidiPort {
    fun open(): Flow<MidiEvent>
}

interface OutputPort : MidiPort {
    fun open(): SendChannel<MidiEvent>
}

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
                isInput && isOutput -> object : MidifunkDevice, InputDevice by wrapInput(device),
                    OutputDevice by wrapOutput(device) {
                    override fun close() {
                        input.close()
                        output.close()
                    }
                }

                isInput && !isOutput -> object : MidifunkDevice, InputDevice by wrapInput(device) {
                    override fun close() = input.close()
                }

                !isInput && isOutput -> object : MidifunkDevice, OutputDevice by wrapOutput(device) {
                    override fun close() = output.close()
                }

                else -> object : MidifunkDevice {
                    override fun close() = Unit
                }
            }
        }

        private fun wrapInput(device: MidiDevice): InputDevice = object : InputDevice {
            override val input: InputPort = object : InputPort {
                val scope = CoroutineScope(Dispatchers.Default)

                val channel = Channel<MidiEvent>()
                val receiver = generateReceiver(channel)
                val flow = channel.consumeAsFlow().shareIn(scope, SharingStarted.WhileSubscribed())

                override fun open(): Flow<MidiEvent> {
                    device.transmitter.receiver = receiver
                    device.open()
                    return flow
                }

                override fun close() {
                    channel.close()
                }
            }
        }

        private fun wrapOutput(device: MidiDevice): OutputDevice = object : OutputDevice {
            override val output: OutputPort = object : OutputPort {
                val scope = CoroutineScope(Dispatchers.Default)

                val channel = Channel<MidiEvent>()

                override fun open(): SendChannel<MidiEvent> {
                    device.open()
                    channel.consumeAsFlow().onEach { device.receiver.send(it.convert(), it.timestamp) }.launchIn(scope)
                    return channel
                }

                override fun close() {
                    channel.close()
                }
            }
        }


        private fun generateReceiver(channel: Channel<MidiEvent>) = object : Receiver {
            override fun close() {
                channel.close()
            }

            override fun send(message: MidiMessage, timeStamp: Long) = runBlocking {
                channel.send(MidiEvent.convert(message, timeStamp))
            }
        }
    }
}


