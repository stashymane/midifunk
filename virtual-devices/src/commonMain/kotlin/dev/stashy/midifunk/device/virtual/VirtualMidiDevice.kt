package dev.stashy.midifunk.device.virtual

import dev.stashy.midifunk.device.MidiDevice
import dev.stashy.midifunk.device.MidiPort

class VirtualMidiDevice(
    override val name: String,
    override val vendor: String,
    override val description: String
) : MidiDevice {
    override val id: String = "$name/$vendor"
    override val version: String
        get() = TODO("Not yet implemented")

    override val input: MidiPort.Input
        get() = TODO("Not yet implemented")
    override val output: MidiPort.Output
        get() = TODO("Not yet implemented")

    override fun close() {

    }
}
