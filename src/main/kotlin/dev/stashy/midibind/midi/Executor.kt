package dev.stashy.midibind.midi

import dev.stashy.midibind.midi.definitions.MidiEvent

class Executor {
    val mods = mutableListOf<(MidiEvent) -> MidiEvent?>()
    val actions = mutableListOf<(MidiEvent) -> Unit>()

    fun sendMessage(msg: MidiEvent) {
        var temp: MidiEvent? = msg
        mods.forEach { f ->
            temp?.let { temp = f(it) }
        }
        temp?.let { filtered ->
            actions.forEach { it(filtered) }
        }
    }

    fun filter(filter: (MidiEvent) -> Boolean): Executor {
        modify { e: MidiEvent -> if (filter(e)) e else null }
        return this
    }

    fun modify(mod: (MidiEvent) -> MidiEvent?): Executor {
        mods += mod
        return this
    }

    fun addAction(action: (MidiEvent) -> Unit): Executor {
        actions += action
        return this
    }
}