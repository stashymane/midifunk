package dev.stashy.midibind

import dev.stashy.midibind.midi.definitions.MidiEvent
import dev.stashy.midibind.midi.definitions.NoteData
import dev.stashy.midibind.midi.definitions.VelocityData
import org.junit.jupiter.api.Test

class ConverterTest {
    @Test
    fun conversionTest() {
        val e = MidiEvent.convert(arrayOf(0x90, 0x01, 0x02))
        assert(e is NoteData)
        if (e is NoteData) assert(e.note == 1)
        assert(e is VelocityData)
        if (e is VelocityData) assert(e.velocity == 2)
    }
}