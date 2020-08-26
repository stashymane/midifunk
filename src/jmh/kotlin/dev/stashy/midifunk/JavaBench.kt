package dev.stashy.midifunk

import org.openjdk.jmh.annotations.*
import javax.sound.midi.MidiMessage
import javax.sound.midi.Receiver

@State(Scope.Benchmark)
class JavaBench {
    val dev = TestDevice()
    lateinit var testMsg: MidiMessage

    @Setup
    fun prepare() {
        testMsg = object : MidiMessage(byteArrayOf(0x00, 0x00, 0x00)) {
            override fun clone(): Any {
                TODO("Not yet implemented")
            }
        }
        dev.transmitter.receiver = object : Receiver {
            var testValue = 0
            override fun send(message: MidiMessage?, timeStamp: Long) {
                testValue++
            }

            override fun close() {}
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    fun send() {
        dev.transmitter.receiver!!.send(testMsg, 0)
    }

    @TearDown
    fun close() {
        dev.close()
    }
}