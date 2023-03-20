package dev.stashy.midifunk.device

import dev.stashy.midifunk.MidiData
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.Flow


interface MidiPort {
    fun close()
}

interface InputPort : MidiPort {
    fun open(): Flow<MidiData>
}

interface OutputPort : MidiPort {
    fun open(): SendChannel<MidiData>
}

