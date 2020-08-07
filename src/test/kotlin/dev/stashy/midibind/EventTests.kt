package dev.stashy.midibind

import dev.stashy.midibind.midi.Executor
import dev.stashy.midibind.midi.definitions.MidiEvent
import dev.stashy.midibind.midi.definitions.NoteData
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class EventTests {
    val noteOn = MidiEvent.convert(arrayOf(0x90, 0x01, 0x02))
    val cc = MidiEvent.convert(arrayOf(0xB0, 0x00, 0x05))

    @Test
    fun conversionTest() {
        assertTrue(noteOn is NoteData)
        if (noteOn is NoteData) assertTrue(noteOn.note == 1)
    }

    @Test
    fun execTest() {
        val a = Executor().filter { noteOn is NoteData }
    }
}