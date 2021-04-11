package dev.stashy.midifunk

import javax.sound.midi.MidiDevice
import javax.sound.midi.Receiver
import javax.sound.midi.Transmitter

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
        private var rec: Receiver? = null

        override fun getReceiver(): Receiver? {
            return rec
        }

        override fun close() {}

        override fun setReceiver(receiver: Receiver?) {
            rec = receiver
        }
    }
}