import dev.stashy.midifunk.events.lsb
import dev.stashy.midifunk.events.msb
import dev.stashy.midifunk.events.withLsb
import dev.stashy.midifunk.events.withMsb
import kotlin.test.Test
import kotlin.test.assertEquals

class ByteOpTest {
    @Test
    fun read() {
        val test = 171u
        assertEquals(10u, test.msb)
        assertEquals(11u, test.lsb)
    }

    @Test
    fun write() {
        var test = 0u
        val msb = 10u
        val lsb = 11u
        test = test.withMsb(msb)
        assertEquals(msb, test.msb)
        test = test.withLsb(lsb)
        assertEquals(lsb, test.lsb)
    }
}
