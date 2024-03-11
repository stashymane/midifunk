package dev.stashy.midifunk.device

interface MidiDevice : AutoCloseable {
    companion object {
        /**
         * Lists all available MIDI devices.
         */
        fun list(): List<MidiDevice> = listMidiDevices()

        /**
         * Gets a MIDI device by its ID.
         * @return The requested MIDI device, or null if it is not present.
         */
        fun get(id: String): MidiDevice? = getMidiDeviceById(id)

        /**
         * Suspends until the device with the specified ID is available. Returns immediately if it is already present.
         * @return The requested MIDI device.
         */
        suspend fun await(id: String): MidiDevice = get(id) ?: DevicePoller.awaitDevice(id).await()
    }

    val id: String
    val name: String
    val vendor: String
    val description: String
    val version: String

    val input: MidiPort.Input
    val output: MidiPort.Output
}
