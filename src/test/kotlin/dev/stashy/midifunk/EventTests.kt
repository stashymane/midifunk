package dev.stashy.midifunk

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import javax.sound.midi.ShortMessage
import kotlin.math.abs

class EventTests {
    companion object Events {
        private val noteOn = ShortMessage()
        private val afterTouch = ShortMessage(ShortMessage.POLY_PRESSURE, 0x01, 0x02)
        private val sysEx = ShortMessage(ShortMessage.START, 0x3, 0x4)

        private val noteEvent = MidiEvent.convert(noteOn)
        private val afterTouchEvent = MidiEvent.convert(afterTouch)
        private val sysExEvent = MidiEvent.convert(sysEx)
    }

    private val testDevice = TestDevice()

    @Test
    fun conversionTest() {
        assertTrue(noteEvent is NoteEvent)
        assertTrue(afterTouchEvent is PressureEvent)
        assertTrue(sysExEvent is SysExEvent)

        if (noteEvent is NoteEvent) assertEquals(noteEvent.note, noteOn.data1.toUInt())
        if (afterTouchEvent is PressureEvent) assertEquals(afterTouchEvent.pressure, afterTouch.data2.toUInt())
        if (sysExEvent is SysExEvent) assertEquals(sysExEvent.sysExType, SysExEvent.Type.Start)
    }

    @Test
    fun creationTest() {
        val polyAftertouch = PressureEvent.create {
            pressure = 1u
        }
    }

    @Test
    fun pitchBendWrite() {
        val min = PitchBendEvent.create { value = -1.0f }
        assertPitchBend(min.data, 0u, 0u, "Minimum")

        val center = PitchBendEvent.create { value = 0.0f }
        assertPitchBend(center.data, 0u, 64u, "Center")

        val max = PitchBendEvent.create { value = 1.0f }
        assertPitchBend(max.data, 127u, 127u, "Maximum")
    }

    private fun assertPitchBend(data: List<UInt>, lsb: UInt, msb: UInt, type: String) {
        assertEquals(lsb to msb, data[1] to data[2], "$type pitch bend bytes are incorrect.")
    }

    @Test
    fun pitchBendRead() {
        /* value retrieved by incrementing the lowest significant byte in a pitchbend event by one,
           and comparing the resulting double with a pitchbend with both bytes as zero.
           this is as much precision as we can guarantee. */
        val precision = 1.8310547E-4f
        val increment = 0.0001f

        val testValues =
            generateSequence(-1.0f) { (it + increment) }.takeWhile { it <= 1.0f } + sequenceOf(-1.0f, 0.0f, 1.0f)
        testValues.forEach {
            println(it)
            val event = PitchBendEvent.create { value = it }
            assertTrue(floatsEqual(it, event.value, precision), "Tested value: $it, received value: ${event.value}")
        }
    }

    fun floatsEqual(a: Float, b: Float, precision: Float): Boolean {
        return abs(a - b) < precision
    }
}
