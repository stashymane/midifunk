package dev.stashy.midifunk

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ByteOpTest {

    @Test
    fun read() {
        val test: UInt = 171u
        assertEquals(10u, test.msb)
        assertEquals(11u, test.lsb)
    }

    @Test
    fun write() {
        var test: UInt = 0u
        val msb: UInt = 10u
        val lsb: UInt = 11u
        test = test.withMsb(msb)
        assertEquals(msb, test.msb)
        test = test.withLsb(lsb)
        assertEquals(lsb, test.lsb)
    }
}
