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
    private val message = ShortMessage()
    private val event = MidiEvent.convert(message.message.mapTo(mutableListOf()) { it.toInt() })

    private val dev = TestDevice()

    @Test
    fun conversionTest() {
        assertTrue(event is NoteData)
        if (event is NoteData) assertTrue(event.note == ShortMessage().data1)
    }

    @Test
    fun replayTest() {
        dev.transmitter.receiver.send(message, event.timestamp)
        val result = runBlocking { dev.receive.whileActive().first() }
        assertEquals(
            event,
            result,
            "Flow returned invalid result: expected ${event.data.joinToString(":")}, got "
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
                    dev.eventReceiver.send(message, -1)
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
