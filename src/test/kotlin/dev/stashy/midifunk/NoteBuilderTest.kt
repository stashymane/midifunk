package dev.stashy.midifunk

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class NoteBuilderTest {
    @Test
    fun testBuild() {
        assertEquals(0u, Note.C(-1))
        assertEquals(11u, Note.B(-1))
        assertEquals(127u, Note.G(9))
    }
}
