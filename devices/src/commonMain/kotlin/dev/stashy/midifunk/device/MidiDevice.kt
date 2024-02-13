package dev.stashy.midifunk.device

interface MidiDevice {
    companion object {
        fun list(): List<MidiDevice> = listMidiDevices()
        fun get(id: String): MidiDevice? = getMidiDeviceById(id)
    }

    val id: String
    val name: String
    val vendor: String
    val description: String
    val version: String
}
