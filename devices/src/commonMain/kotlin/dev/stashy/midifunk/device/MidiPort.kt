package dev.stashy.midifunk.device

import dev.stashy.midifunk.events.MidiData
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel

interface MidiPort<T>: AutoCloseable {
    val isPresent: Boolean

    interface Input : MidiPort<ReceiveChannel<MidiData>>
    interface Output : MidiPort<SendChannel<MidiData>>
}
