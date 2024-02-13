import javax.sound.midi.MidiDevice
import javax.sound.midi.MidiMessage
import javax.sound.midi.Receiver
import javax.sound.midi.Transmitter

class TestDevice : MidiDevice {
    var sendCallback: (MidiMessage) -> Unit = {}

    private var isOpen = false

    private val transmitter = TestTransmitter()
    private val receiver = object : Receiver {
        override fun close() {
        }

        override fun send(message: MidiMessage, timeStamp: Long) {
            sendCallback(message)
        }
    }

    override fun getDeviceInfo(): MidiDevice.Info {
        return object : MidiDevice.Info("Test", "stashymane", "Test device", "test") {}
    }

    override fun getReceiver(): Receiver {
        return receiver
    }

    override fun open() {
        isOpen = true
    }

    override fun getTransmitters(): MutableList<Transmitter> {
        return mutableListOf()
    }

    override fun getReceivers(): MutableList<Receiver> {
        return mutableListOf()
    }

    override fun getMaxReceivers(): Int {
        return 1
    }

    override fun isOpen(): Boolean {
        return isOpen
    }

    override fun getMicrosecondPosition(): Long {
        return System.currentTimeMillis()
    }

    override fun close() {
        isOpen = false
        transmitter.close()
    }

    override fun getTransmitter(): Transmitter {
        return transmitter
    }

    override fun getMaxTransmitters(): Int {
        return 1
    }

    inner class TestTransmitter : Transmitter {
        private var rec: Receiver? = null

        override fun getReceiver(): Receiver? {
            return rec
        }

        override fun setReceiver(receiver: Receiver) {
            rec = receiver
        }

        override fun close() {
            rec?.close()
        }
    }
}
