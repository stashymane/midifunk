import dev.stashy.midifunk.events.Note
import kotlin.test.Test
import kotlin.test.assertEquals

class NoteBuilderTest {
    @Test
    fun testBuild() {
        assertEquals(0u, Note.C(-1))
        assertEquals(11u, Note.B(-1))
        assertEquals(127u, Note.G(9))
    }
}
