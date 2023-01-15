package dev.stashy.midifunk

import javax.sound.midi.MidiDevice
import javax.sound.midi.MidiSystem

object Midifunk {
    val descriptors: List<MidiDevice.Info>
        get() = MidiSystem.getMidiDeviceInfo().asList()
}

val Collection<MidiDevice.Info>.inputs: List<MidiDevice.Info>
    get() = this.filter { MidiSystem.getMidiDevice(it).maxReceivers != -1 }

val Collection<MidiDevice.Info>.outputs: List<MidiDevice.Info>
    get() = this.filter { MidiSystem.getMidiDevice(it).maxTransmitters != -1 }
