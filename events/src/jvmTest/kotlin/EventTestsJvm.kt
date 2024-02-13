import dev.stashy.midifunk.events.*
import junit.framework.TestCase.assertEquals
import javax.sound.midi.ShortMessage
import kotlin.test.Test
import kotlin.test.assertTrue

class EventTestsJvm {
    companion object Events {
        private val noteOn = ShortMessage()
        private val afterTouch = ShortMessage(ShortMessage.POLY_PRESSURE, 0x01, 0x02)
        private val sysEx = ShortMessage(ShortMessage.START, 0x3, 0x4)

        private val noteEvent = MidiEvent.convert(noteOn.data())
        private val afterTouchEvent = MidiEvent.convert(afterTouch.data())
        private val sysExEvent = MidiEvent.convert(sysEx.data())
    }

    @Test
    fun conversionTest() {
        assertTrue(noteEvent is NoteEvent)
        assertTrue(afterTouchEvent is PressureEvent)
        assertTrue(sysExEvent is SysExEvent)

        assertEquals(noteEvent.note, noteOn.data1.toUInt())
        assertEquals(afterTouchEvent.pressure, afterTouch.data2.toUInt())
        assertEquals(sysExEvent.sysExType, SysExEvent.Type.Start)
    }
}
