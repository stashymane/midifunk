import dev.stashy.midifunk.device.MidiDevice
import dev.stashy.midifunk.device.MidiDeviceJvm
import dev.stashy.midifunk.events.NoteEvent
import dev.stashy.midifunk.events.toJvmMessage
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.sound.midi.MidiMessage
import kotlin.test.Test
import kotlin.test.assertEquals

class DeviceTest {
    @Test
    fun testOutput() = runBlocking {
        val n = 10
        var received = 0

        val device = MidiDeviceJvm(testDevice {
            received++
        })

        coroutineScope {
            val channel = device.output.open(this)
            repeat(n) {
                channel.send(NoteEvent.create {
                    note = it.toUInt()
                    velocity = 127u
                })
            }
            channel.close()
        }
        device.close()
        assertEquals(n, received, "Not all messages have been sent.")
    }

    @Test
    fun testInput() = runBlocking {
        val n = 0
        var received = 0

        val testDevice = testDevice {}
        val device: MidiDevice = MidiDeviceJvm(testDevice)

        coroutineScope {
            val channel = device.input.open(this)
            launch {
                channel.consumeEach { received++ }
            }
            repeat(n) {
                testDevice.receiver.send(NoteEvent.create {
                    note = it.toUInt()
                    velocity = 127u
                }.toJvmMessage(), 0)
            }
            device.close()
        }


        assertEquals(n, received, "Received a different amount of messages than has been sent.")
    }

    private fun testDevice(onReceive: (MidiMessage) -> Unit): TestDeviceJvm =
        TestDeviceJvm().apply { sendCallback = onReceive }
}
