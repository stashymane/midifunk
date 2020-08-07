package dev.stashy.midibind

import dev.stashy.midibind.midi.Executor
import dev.stashy.midibind.midi.definitions.MidiEvent
import dev.stashy.midibind.midi.definitions.NoteData
import dev.stashy.midibind.midi.definitions.VelocityData
import org.junit.jupiter.api.Test

class EventTests {
    val noteOn = MidiEvent.convert(arrayOf(0x90, 0x01, 0x02))
    val cc = MidiEvent.convert(arrayOf(0xB0, 0x00, 0x05))

    @Test
    fun conversionTest() {
        assert(noteOn is NoteData)
        if (noteOn is NoteData) assert(noteOn.note == 1)
        assert(noteOn is VelocityData)
        if (noteOn is VelocityData) assert(noteOn.velocity == 2)
    }

    @Test
    fun execTest() {
        val a = Executor().filter { noteOn is NoteData }
    }
}