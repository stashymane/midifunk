package dev.stashy.midifunk

import org.openjdk.jmh.annotations.*
import javax.sound.midi.MidiMessage

@State(Scope.Benchmark)
class MidifunkBench {
    val id = InputDevice(TestDevice())
    var testValue = 0
    lateinit var testMsg: MidiMessage

    @Setup
    fun prepare() {
        testMsg = object : MidiMessage(byteArrayOf(0x00, 0x00, 0x00)) {
            override fun clone(): Any {
                TODO("Not yet implemented")
            }
        }
        id.receivers += EventReceiver().addAction { testValue++ }
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    fun bench() {
        id.dev.transmitter.receiver!!.send(testMsg, 0)
    }

    @TearDown
    fun close() {
        id.close()
    }
}
