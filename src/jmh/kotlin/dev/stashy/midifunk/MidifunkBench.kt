package dev.stashy.midifunk

import org.openjdk.jmh.annotations.*
import javax.sound.midi.MidiMessage

@State(Scope.Benchmark)
class MidifunkBench {
    val dev = TestDevice()
    var testValue = 0
    lateinit var testMsg: MidiMessage

    @Setup
    fun prepare() {
        testMsg = object : MidiMessage(byteArrayOf(0x00, 0x00, 0x00)) {
            override fun clone(): Any {
                TODO("Not yet implemented")
            }
        }
        dev.from.subscribe { testValue++ }
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    fun send() {
        dev.transmitter.receiver!!.send(testMsg, 0)
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @Timeout(time = 2)
    fun convert() {
        MidiEvent.convert(mutableListOf<Int>(0x90, 0x01, 0x02))
    }

    @TearDown
    fun close() {
        dev.close()
    }
}
