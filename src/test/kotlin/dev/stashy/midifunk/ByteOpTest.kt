package dev.stashy.midifunk

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ByteOpTest {

    @Test
    fun read() {
        val test = 0xAB
        assertEquals(0xA, test.msb)
        assertEquals(0xB, test.lsb)
    }

    @Test
    fun write() {
        var test = 0x00
        val msb = 0xA
        val lsb = 0xB
        test = test.withMsb(msb)
        assertEquals(msb, test.msb)
        test = test.withLsb(lsb)
        assertEquals(lsb, test.lsb)
    }
}