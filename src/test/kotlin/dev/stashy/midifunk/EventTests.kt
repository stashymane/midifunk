package dev.stashy.midifunk

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.junit.jupiter.api.Assertions.*
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

    private val testDevice = TestDevice()

    @Test
    fun conversionTest() {
        assertTrue(noteEvent is NoteData)
        assertTrue(afterTouchEvent is PressureData)
        assertTrue(sysExEvent is SysExData)

        if (noteEvent is NoteData) assertEquals(noteEvent.note, noteOn.data1.toUInt())
        if (afterTouchEvent is PressureData) assertEquals(afterTouchEvent.note, afterTouch.data1.toUInt())
        if (sysExEvent is SysExData) assertEquals(sysExEvent.type, SysExData.Type.Start)
    }

    @Test
    fun inputTest() {
        assertTrue(!testDevice.isOpen, "Device opened before test began")

        val n = 5
        val scope = CoroutineScope(Dispatchers.Default)
        val midifunkDevice = testDevice.asMidifunk()

        var collected = 0
        midifunkDevice.input.onEach { collected++ }.launchIn(scope)

        scope.ensureActive()
        repeat(n) {
            testDevice.transmitter.receiver.send(ShortMessage(), 0)
        }
        scope.cancel()

        assertEquals(n, collected, "Did not collect all sent events")
    }

    @Test
    fun outputTest() {
        assertTrue(!testDevice.isOpen, "Device opened before test began")

        val n = 5
        var output = 0
        val midifunkDevice = testDevice.asMidifunk()

        testDevice.sendCallback = { output++ }

        runBlocking {
            repeat(n) {
                midifunkDevice.outputChannel.send(noteEvent)
            }
        }

        assertEquals(n, output, "Messages were not output")
    }

    @Test
    fun testAutoClose() {
        val n = 5
        assertTrue(!testDevice.isOpen, "Device opened before test began")

        val scope = CoroutineScope(Dispatchers.Default)
        val midifunkDevice = testDevice.asMidifunk()

        val jobs = (0..n).map {
            midifunkDevice.input.launchIn(scope)
        }

        runBlocking {
            jobs.forEachIndexed { i, job ->
                job.cancelAndJoin()
                if (i < n) {
                    assertTrue(testDevice.isOpen, "Not all listeners are removed, device should be open")
                } else {
                    assertFalse(testDevice.isOpen, "Last listener removed, device should be closed")
                }
            }
        }
        scope.cancel()
        assertFalse(scope.isActive, "Scope should not be active")
    }
}
