package dev.stashy.midifunk.device

class MidiDeviceJvm(private val descriptor: javax.sound.midi.MidiDevice.Info, index: Int) : MidiDevice {
    override val id: String = "${descriptor.name} #${index + 1}"
    override val name: String = descriptor.name
    override val vendor: String = descriptor.vendor
    override val description: String = descriptor.description
    override val version: String = descriptor.version
}
