package dev.stashy.midifunk

import javax.sound.midi.*

fun main() {
    val t = TimeTest()
    t.java()
    t.midifunk()
}

class TimeTest {
    val dev = TestDevice()
    var testValue = 0

    fun java() {
        testValue = 0
        dev.transmitter.receiver = object : Receiver {
            override fun send(message: MidiMessage?, timeStamp: Long) {
                testValue++
            }

            override fun close() {}
        }
        val before = System.currentTimeMillis()
        spam()
        val after = System.currentTimeMillis()
        println("Java took ${after - before}ms")
    }

    fun midifunk() {
        testValue = 0
        val d = Device(dev)
        d.receivers += EventReceiver().addAction { testValue++ }
        val before = System.currentTimeMillis()
        spam()
        val after = System.currentTimeMillis()
        d.close()
        println("MF took ${after - before}ms")
    }

    fun spam() {
        for (i in 1..100000000)
            dev.transmitter.receiver!!.send(object : MidiMessage(byteArrayOf(0x90.toByte(), 0x0, 0x0)) {
                override fun clone(): Any {
                    TODO("Not yet implemented")
                }
            }, System.currentTimeMillis())
    }
}

class TestDevice : MidiDevice {
    val transmitter = TestTransmitter()

    override fun getDeviceInfo(): MidiDevice.Info {
        return object : MidiDevice.Info("Test", "stashymane", "Test device", "test") {}
    }

    override fun getReceiver(): Receiver {
        TODO("Not yet implemented")
    }

    override fun open() {}

    override fun getTransmitters(): MutableList<Transmitter> {
        return mutableListOf()
    }

    override fun getReceivers(): MutableList<Receiver> {
        return mutableListOf()
    }

    override fun getMaxReceivers(): Int {
        return 0
    }

    override fun isOpen(): Boolean {
        return true
    }

    override fun getMicrosecondPosition(): Long {
        return System.currentTimeMillis()
    }

    override fun close() {}

    override fun getTransmitter(): Transmitter {
        return transmitter
    }

    override fun getMaxTransmitters(): Int {
        return 1
    }

    class TestTransmitter : Transmitter {
        var rec: Receiver? = null

        override fun getReceiver(): Receiver? {
            return rec
        }

        override fun close() {}

        override fun setReceiver(receiver: Receiver?) {
            rec = receiver
        }

    }
}