package dev.stashy.midifunk

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import javax.sound.midi.ShortMessage

class EventTests {
    val noteOn = MidiEvent.convert(mutableListOf(0x90, 0x01, 0x02))
    val cc = MidiEvent.convert(mutableListOf(0xB0, 0x00, 0x05))
    val noteOnArr = byteArrayOf(0x00, 0x00)
    val dev = TestDevice()

    @Test
    fun conversionTest() {
        assertTrue(noteOn is NoteData)
        if (noteOn is NoteData) assertTrue(noteOn.note == 1)
    }

    @Test
    fun fromTest() {
        var ran = false
        dev.transmitter.receiver?.send(ShortMessage(), 0)
        dev.from.lastOrError().doOnError { ran = false }.doAfterSuccess { ran = true }
            .doFinally { assertTrue(ran, "Event was not received.") }.subscribe()
    }
}