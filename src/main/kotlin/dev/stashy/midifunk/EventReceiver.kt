package dev.stashy.midifunk

class EventReceiver {
    val enabled = true
    val mods = mutableListOf<(MidiEvent) -> MidiEvent?>()
    val actions = mutableListOf<(MidiEvent) -> Unit>()

    fun sendMessage(msg: MidiEvent) {
        if (!enabled) return
        var temp: MidiEvent? = msg
        mods.forEach { f ->
            temp?.let { temp = f(it) }
        }
        temp?.let { filtered ->
            actions.forEach { it(filtered) }
        }
    }

    fun filter(filter: (MidiEvent) -> Boolean): EventReceiver {
        return modify { e: MidiEvent -> if (filter(e)) e else null }
    }

    fun modify(mod: (MidiEvent) -> MidiEvent?): EventReceiver {
        mods += mod
        return this
    }

    fun addAction(action: (MidiEvent) -> Unit): EventReceiver {
        actions += action
        return this
    }
}