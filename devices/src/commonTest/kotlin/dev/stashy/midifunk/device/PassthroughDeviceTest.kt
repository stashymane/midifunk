package dev.stashy.midifunk.device

import dev.stashy.midifunk.events.NoteEvent
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class PassthroughDeviceTest {
    @Test
    fun testPassthrough(): Unit = runBlocking {
        val n = 10
        var received = 0

        val device: MidiDevice = PassthroughDevice()

        coroutineScope {
            launch {
                val input = device.input.open(this)
                input.consumeEach { received++ }
            }
            launch {
                val output = device.output.open(this)
                repeat(n) {
                    output.send(NoteEvent.create {
                        note = it.toUInt()
                        velocity = 127u
                    })
                }
                device.close()
            }
        }

        assertEquals(n, received)
    }
}
