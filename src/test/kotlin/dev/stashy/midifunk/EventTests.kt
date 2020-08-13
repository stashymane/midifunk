package dev.stashy.midifunk

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class EventTests {
    val noteOn = MidiEvent.convert(mutableListOf(0x90, 0x01, 0x02))
    val cc = MidiEvent.convert(mutableListOf(0xB0, 0x00, 0x05))

    @Test
    fun conversionTest() {
        assertTrue(noteOn is NoteData)
        if (noteOn is NoteData) assertTrue(noteOn.note == 1)
    }

    @Test
    fun filterTest() {
        var ran = false
        val a = EventReceiver()
            .filter { it is NoteData }.addAction { ran = true }
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
        val a = EventReceiver()
            .modify { (it as NoteData).velocity = vel; it }.addAction { test = (it as NoteData).velocity }
        a.sendMessage(noteOn)
        assertEquals(vel, test)
    }
}