package dev.stashy.midifunk.device

internal expect fun listMidiDevices(): List<MidiDevice>
internal expect fun getMidiDeviceById(id: String): MidiDevice?
