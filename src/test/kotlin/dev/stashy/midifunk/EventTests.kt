package dev.stashy.midifunk

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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

    private val testDevice = TestDevice()

    @Test
    fun conversionTest() {
        assertTrue(noteEvent is NoteEvent)
        assertTrue(afterTouchEvent is PressureEvent)
        assertTrue(sysExEvent is SysExEvent)

        if (noteEvent is NoteEvent) assertEquals(noteEvent.note, noteOn.data1.toUInt())
        if (afterTouchEvent is PressureEvent) assertEquals(afterTouchEvent.note, afterTouch.data1.toUInt())
        if (sysExEvent is SysExEvent) assertEquals(sysExEvent.type, SysExEvent.Type.Start)
    }

    @Test
    fun inputTest() {
        val n = 5
        var collected = 0
        val scope = CoroutineScope(Dispatchers.Default)

        val mf = testDevice.asMidifunk()
        assertTrue(mf is InputDevice)
        val iDevice = mf as InputDevice

        val job = iDevice.input.open().onEach { collected++ }.launchIn(scope)
        job.ensureActive()

        repeat(n) {
            testDevice.transmitter.receiver.send(noteOn, 0)
        }

        runBlocking {
            job.cancelAndJoin()
            assertEquals(n, collected, "All input messages were not received")
        }

        mf.close()
    }

    @Test
    fun outputTest() {
        val n = 5
        var collected = 0
        val scope = CoroutineScope(Dispatchers.Default)

        val mf = testDevice.asMidifunk()
        testDevice.sendCallback = { collected++ }
        assertTrue(mf is OutputDevice)
        val oDevice = mf as OutputDevice

        val channel = oDevice.output.open()
        runBlocking {
            repeat(n) {
                channel.send(noteEvent)
            }
        }

        assertEquals(n, collected, "All output messages were not received")
        mf.close()
    }
}
