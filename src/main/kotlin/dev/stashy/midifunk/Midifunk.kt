package dev.stashy.midifunk

import javax.sound.midi.MidiDevice
import javax.sound.midi.MidiSystem

object Midifunk {
    val descriptors: List<MidiDevice.Info>
        get() = MidiSystem.getMidiDeviceInfo().asList()
}

val MidiDevice.Info.device: MidiDevice
    get() = MidiSystem.getMidiDevice(this)

val Collection<MidiDevice.Info>.inputs: List<MidiDevice.Info>
    get() = this.filter { it.device.maxTransmitters != -1 }

val Collection<MidiDevice.Info>.outputs: List<MidiDevice.Info>
    get() = this.filter { it.device.maxReceivers != -1 }
