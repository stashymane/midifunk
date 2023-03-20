package dev.stashy.midifunk

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import javax.sound.midi.ShortMessage

class DeviceTests {
    companion object Events {
        private val noteOn = ShortMessage()
        private val noteEvent = MidiEvent.convert(noteOn) as NoteEvent
    }

    private val testDevice = TestDevice()

    @Test
    fun inputTest() {
        val n = 5
        var collected = 0
        val scope = CoroutineScope(Dispatchers.Default)

        val mf = testDevice.asMidifunk()
        Assertions.assertTrue(mf is InputDevice)
        val iDevice = mf as InputDevice

        val job = iDevice.input.open().onEach { collected++ }.launchIn(scope)
        job.ensureActive()

        repeat(n) {
            testDevice.transmitter.receiver.send(noteOn, 0)
        }

        runBlocking {
            job.cancelAndJoin()
            Assertions.assertEquals(n, collected, "All input messages were not received")
        }

        mf.close()
    }

    @Test
    fun outputTest() {
        val n = 5
        var collected = 0
        val scope = CoroutineScope(Dispatchers.Default)

        val dev = testDevice.asMidifunk()
        testDevice.sendCallback = { collected++ }
        Assertions.assertTrue(dev is OutputDevice)

        val channel = (dev as OutputDevice).output.open()
        runBlocking {
            repeat(n) { channel.send(noteEvent) }
        }

        Assertions.assertEquals(n, collected, "All output messages were not received")
        dev.close()

        val device = testDevice.asMidifunk() as OutputDevice
        val c: SendChannel<MidiEvent> = device.output.open()
        c.trySend(MidiEvent(mutableListOf(9u)))
    }
}
