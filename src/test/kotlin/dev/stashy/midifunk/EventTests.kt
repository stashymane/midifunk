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
        val min = PitchBendEvent.create { value = 0u }
        assertEquals(0u to 0u, min.data[1] to min.data[2], "Minimum pitch bend value has not been set correctly.")

        val center = PitchBendEvent.create { value = PitchBendEvent.CENTER }
        assertEquals(0u to 64u, center.data[1] to center.data[2], "Center pitch bend value has not been set correctly.")

        val max = PitchBendEvent.create { value = PitchBendEvent.MAX_VALUE }
        assertEquals(127u to 127u, max.data[1] to max.data[2], "Maximum pitch bend value has not been set correctly.")
    }

    @Test
    fun pitchBendRead() {
        val min = PitchBendEvent.create {
            data[1] = 0u
            data[2] = 0u
        }
        assertEquals(0u, min.value, "Minimum pitch bend value has not been retrieved correctly.")

        val center = PitchBendEvent.create {
            data[1] = 0u
            data[2] = 64u
        }
        assertEquals(PitchBendEvent.CENTER, center.value, "Center pitch bend value has not been retrieved correctly.")

        val max = PitchBendEvent.create {
            data[1] = 127u
            data[2] = 127u
        }
        assertEquals(PitchBendEvent.MAX_VALUE, max.value, "Maximum pitch bend value has not been retrieved correctly.")
    }

    @Test
    fun pitchBendRangeWrite() {
        val min = PitchBendEvent.create { range = -1.0f }
        assertPitchBend(min.data, 0u, 0u, "Minimum")

        val center = PitchBendEvent.create { range = 0.0f }
        assertPitchBend(center.data, 0u, 64u, "Center")

        val max = PitchBendEvent.create { range = 1.0f }
        assertPitchBend(max.data, 127u, 127u, "Maximum")
    }

    private fun assertPitchBend(data: List<UInt>, lsb: UInt, msb: UInt, type: String) {
        assertEquals(lsb to msb, data[1] to data[2], "$type pitch bend bytes are incorrect.")
    }

    @Test
    fun pitchBendRangeRead() {
        /* value retrieved by incrementing the lowest significant byte in a pitchbend event by one,
           and comparing the resulting double with a pitchbend with both bytes as zero.
           this is as much precision as we can guarantee. */
        val precision = 1.8310547E-4f
        val increment = 0.0001f

        val testValues =
            generateSequence(-1.0f) { (it + increment) }.takeWhile { it <= 1.0f } + sequenceOf(0.0f, 1.0f)
        testValues.forEach {
            val event = PitchBendEvent.create { range = it }
            assertTrue(floatsEqual(it, event.range, precision), "Tested value: $it, received value: ${event.range}")
        }
    }

    fun floatsEqual(a: Float, b: Float, precision: Float): Boolean {
        return abs(a - b) < precision
    }
}
