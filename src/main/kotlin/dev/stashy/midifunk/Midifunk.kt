package dev.stashy.midifunk

import javax.sound.midi.MidiDevice
import javax.sound.midi.MidiSystem

object Midifunk {
    val descriptors: Array<MidiDevice.Info>
        get() = MidiSystem.getMidiDeviceInfo()
}

val MidiDevice.Info.device: MidiDevice
    get() = MidiSystem.getMidiDevice(this)
