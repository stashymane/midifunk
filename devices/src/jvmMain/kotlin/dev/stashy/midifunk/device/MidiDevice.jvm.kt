package dev.stashy.midifunk.device

import dev.stashy.midifunk.events.MidiData
import dev.stashy.midifunk.events.MidiEvent
import dev.stashy.midifunk.events.data
import dev.stashy.midifunk.events.toJvmMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.sound.midi.MidiMessage
import javax.sound.midi.MidiSystem
import javax.sound.midi.Receiver
import javax.sound.midi.MidiDevice as JavaMidiDevice

class MidiDeviceJvm(private val device: JavaMidiDevice, index: Int = 0) : MidiDevice {
    constructor(info: JavaMidiDevice.Info, index: Int = 0) : this(MidiSystem.getMidiDevice(info), index)

    override val id: String = "jvm-${device.deviceInfo.hashCode().toString(16)}"
    override val name: String = device.deviceInfo.name
    override val vendor: String = device.deviceInfo.vendor
    override val description: String = device.deviceInfo.description
    override val version: String = device.deviceInfo.version

    override val input: MidiPort.Input = InputPort()
    override val output: MidiPort.Output = OutputPort()

    override fun close() {
        input.close()
        output.close()
        device.close()
    }

    private inner class InputPort : MidiPort.Input {
        private val channel = Channel<MidiData>().apply {
            invokeOnClose {
                device.transmitter?.receiver?.close()
                device.transmitter?.close()
            }
        }

        override val isPresent: Boolean
            get() = device.maxReceivers != -1

        override fun open(scope: CoroutineScope): ReceiveChannel<MidiData> {
            device.open()
            device.transmitter.receiver = channelReceiver(channel)
            return channel
        }

        override fun close() {
            channel.close()
        }
    }

    private inner class OutputPort : MidiPort.Output {
        private val channel = Channel<MidiData>().apply {
            invokeOnClose {
                device.receiver?.close()
            }
        }

        override val isPresent: Boolean
            get() = device.maxTransmitters != -1

        override fun open(scope: CoroutineScope): SendChannel<MidiData> {
            device.open()
            scope.launch {
                for (data in channel) {
                    device.receiver.send(data.toJvmMessage(), data.timestamp)
                }
            }
            return channel
        }

        override fun close() {
            channel.close()
        }
    }

    private fun channelReceiver(channel: Channel<MidiData>) = object : Receiver {
        override fun close() {
            channel.close()
        }

        override fun send(message: MidiMessage, timeStamp: Long) = runBlocking {
            channel.send(MidiEvent.convert(message.data(), timeStamp))
        }
    }

}
