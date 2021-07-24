package dev.stashy.midifunk

import javax.sound.midi.MidiDevice
import javax.sound.midi.MidiSystem

object Midifunk {
    val descriptors: List<MidiDevice.Info>
        get() = MidiSystem.getMidiDeviceInfo().asList()
}

val MidiDevice.Info.device: MidiDevice
    get() = MidiSystem.getMidiDevice(this)
