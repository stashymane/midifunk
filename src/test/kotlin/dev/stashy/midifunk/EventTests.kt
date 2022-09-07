package dev.stashy.midifunk

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import javax.sound.midi.ShortMessage

class EventTests {
    val message = ShortMessage()
    val event = MidiEvent.convert(message.message.mapTo(mutableListOf()) { it.toInt() })

    val dev = TestDevice()

    @Test
    fun conversionTest() {
        assertTrue(event is NoteData)
        if (event is NoteData) assertTrue(event.note == ShortMessage().data1)
    }

    @Test
    fun fromTest() {
        dev.transmitter.receiver.send(message, event.timestamp)
        val result = runBlocking { dev.from.first() }
        assertEquals(event, result, event.data.joinToString(":") + " != " + result?.data?.joinToString(":"))
    }
}