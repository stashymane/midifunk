package dev.stashy.midifunk.device

import dev.stashy.midifunk.events.MidiData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel

class PassthroughDevice : MidiDevice {
    override val id: String = "passthrough"
    override val name: String = "Test passthrough device"
    override val vendor: String = "stashymane"
    override val description: String = "A device to pass through data directly from output to input."
    override val version: String = "1.0.0"

    private val channel = Channel<MidiData>()

    override val input: MidiPort.Input = object : MidiPort.Input {
        var isOpen = false
        override val isPresent: Boolean = true

        override fun open(scope: CoroutineScope): ReceiveChannel<MidiData> = channel

        override fun close() {
            channel.close()
            isOpen = false
        }
    }

    override val output: MidiPort.Output = object : MidiPort.Output {
        var isOpen = false
        override val isPresent: Boolean = true

        override fun open(scope: CoroutineScope): SendChannel<MidiData> = channel

        override fun close() {
            channel.close()
            isOpen = false
        }
    }

    override fun close() {
        input.close()
        output.close()
    }
}
