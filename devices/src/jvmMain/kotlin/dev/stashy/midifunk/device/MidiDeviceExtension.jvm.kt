package dev.stashy.midifunk.device

import javax.sound.midi.MidiSystem

// infos are indexed based on name as there is no way to distinguish between identical devices on JVM
internal actual fun listMidiDevices(): List<MidiDevice> =
    MidiSystem.getMidiDeviceInfo().groupBy { it.name }
        .flatMap { it.value.mapIndexed { index, info -> MidiDeviceJvm(info, index) } }

internal actual fun getMidiDeviceById(id: String): MidiDevice? =
    listMidiDevices().find { it.id == id }
