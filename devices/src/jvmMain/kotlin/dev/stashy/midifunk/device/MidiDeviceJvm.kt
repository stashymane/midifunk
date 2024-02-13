package dev.stashy.midifunk.device

import dev.stashy.midifunk.events.MidiData
import dev.stashy.midifunk.events.MidiEvent
import dev.stashy.midifunk.events.data
import dev.stashy.midifunk.events.toJvmMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.sound.midi.MidiMessage
import javax.sound.midi.MidiSystem
import javax.sound.midi.Receiver

class MidiDeviceJvm(private val device: javax.sound.midi.MidiDevice, index: Int) : MidiDevice {
    constructor(info: javax.sound.midi.MidiDevice.Info, index: Int) : this(MidiSystem.getMidiDevice(info), index)

    override val id: String = "${device.deviceInfo.name} #${index + 1}"
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
        private val channel = Channel<MidiData>()

        override val isPresent: Boolean
            get() = device.maxReceivers != -1

        override fun open(scope: CoroutineScope): ReceiveChannel<MidiData> {
            device.open()
            device.transmitter.receiver = channelReceiver(channel)
            return channel
        }

        override fun close() {
            device.transmitter.close()
            channel.close()
        }
    }

    private inner class OutputPort : MidiPort.Output {
        private val channel = Channel<MidiData>()

        override val isPresent: Boolean
            get() = device.maxTransmitters != -1

        override fun open(scope: CoroutineScope): SendChannel<MidiData> {
            device.open()
            scope.launch {
                channel.consumeEach {
                    device.receiver.send(it.toJvmMessage(), it.timestamp)
                }
            }
            return channel
        }

        override fun close() {
            device.transmitter.receiver.close()
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
