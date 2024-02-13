package dev.stashy.midifunk.device

import dev.stashy.midifunk.device.exceptions.PortUnavailableException
import dev.stashy.midifunk.events.MidiData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel

interface MidiPort<T> : AutoCloseable {
    /**
     * Whether the port is available to open.
     * If this returns false, calling [open] will result in a [PortUnavailableException].
     */
    val isPresent: Boolean

    /**
     * Opens the device & port and returns the according channel.
     * @throws PortUnavailableException when the port is not available on this device.
     */
    fun open(scope: CoroutineScope): T

    interface Input : MidiPort<ReceiveChannel<MidiData>>
    interface Output : MidiPort<SendChannel<MidiData>>
}
