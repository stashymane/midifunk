package dev.stashy.midifunk.device

interface MidiDevice : AutoCloseable {
    companion object {
        /**
         * Lists all available MIDI devices.
         */
        fun list(): List<MidiDevice> = listMidiDevices()

        /**
         * Gets a MIDI device by its ID.
         * @return The MIDI device, or null if it is not present.
         */
        fun get(id: String): MidiDevice? = getMidiDeviceById(id)
    }

    val id: String
    val name: String
    val vendor: String
    val description: String
    val version: String

    val input: MidiPort.Input
    val output: MidiPort.Output
}
