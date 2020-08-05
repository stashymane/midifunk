package dev.stashy.midibind.midi

import dev.stashy.midibind.midi.definitions.MidiEvent

class Executor {
    val filters = mutableListOf<(MidiEvent) -> MidiEvent?>()
    val actions = mutableListOf<(MidiEvent) -> Unit>()

    fun sendMessage(msg: MidiEvent) {
        var temp: MidiEvent? = msg
        filters.forEach { f ->
            temp?.let { temp = f.invoke(it) }
        }
        temp?.let { filtered ->
            actions.forEach { it.invoke(filtered) }
        }
    }

    fun filter(filter: (MidiEvent) -> MidiEvent?): Executor {
        filters += filter
        return this
    }

    fun addAction(action: (MidiEvent) -> Unit): Executor {
        actions += action
        return this
    }
}