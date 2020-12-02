package dev.stashy.midifunk

import java.text.SimpleDateFormat
import java.util.*
import javax.sound.midi.MidiDevice
import javax.sound.midi.MidiSystem

object Midifunk {
    val deviceInfos: Array<MidiDevice.Info>
        get() = MidiSystem.getMidiDeviceInfo()

    init {
        Midifunk.deviceInfos[0].device.let { device ->
            device.open()
            device.from.filter { it is NoteData }.subscribe { /* you got a note on or off event! */ }
        }
    }
}

val MidiDevice.Info.device: MidiDevice
    get() = MidiSystem.getMidiDevice(this)