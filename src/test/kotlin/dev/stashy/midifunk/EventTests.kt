package dev.stashy.midifunk

import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import javax.sound.midi.ShortMessage

class EventTests {
    companion object Events {
        private val noteOn = ShortMessage()
        private val afterTouch = ShortMessage(ShortMessage.POLY_PRESSURE, 0x01, 0x02)
        private val sysEx = ShortMessage(ShortMessage.START, 0x3, 0x4)

        private val noteEvent = convert(noteOn)
        private val afterTouchEvent = convert(afterTouch)
        private val sysExEvent = convert(sysEx)

        private fun convert(msg: ShortMessage): MidiEvent =
            MidiEvent.convert(msg.message.mapTo(mutableListOf()) { it.toUInt() })
    }

    private val dev = TestDevice()

    @Test
    fun conversionTest() {
        assertTrue(noteEvent is NoteData)
        assertTrue(afterTouchEvent is PressureData)
        assertTrue(sysEx is SysExData)

        if (noteEvent is NoteData) assertEquals(noteEvent.note, noteOn.data1.toUInt())
        if (afterTouchEvent is PressureData) assertEquals(afterTouchEvent.note, afterTouch.data1.toUInt())
        if (sysEx is SysExData) assertEquals(sysExEvent.data.first(), sysEx.data1.toUInt())
    }

    @Test
    fun replayTest() {
        dev.transmitter.receiver.send(noteOn, noteEvent.timestamp)
        val result = runBlocking { dev.receive.whileActive().first() }
        assertEquals(
            noteEvent,
            result,
            "Flow returned invalid result: expected ${noteEvent.data.joinToString(":")}, got "
                    + result.data.joinToString(":")
        )
    }

    @Test
    fun collectTest() = runBlocking {
        val n = 5
        val signal = MutableSharedFlow<Unit>()

        launch {
            signal.take(1).collect {
                repeat(n) {
                    dev.eventReceiver.send(noteOn, -1)
                }
                dev.close()
            }
        }

        val count = async {
            dev.receive.onSubscription { signal.emit(Unit) }.whileActive().count()
        }

        assertEquals(n, count.await(), "Received messages do not match sent messages.")

        return@runBlocking
    }
}
