package dev.stashy.midibind

import dev.stashy.midibind.midi.Executor
import dev.stashy.midibind.midi.definitions.MidiEvent
import dev.stashy.midibind.midi.definitions.NoteData
import org.junit.jupiter.api.Assertions.assertEquals
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
    fun filterTest() {
        var ran = false
        val a = Executor().filter { it is NoteData }.addAction { ran = true }
        a.sendMessage(cc)
        assertTrue(!ran) //TODO check why it actually runs here
        ran = false
        a.sendMessage(noteOn)
        assertTrue(ran)
    }

    @Test
    fun modTest() {
        val vel = 5
        var test = -1
        val a = Executor().modify { (it as NoteData).velocity = vel; it }.addAction { test = (it as NoteData).velocity }
        a.sendMessage(noteOn)
        assertEquals(vel, test)
    }
}