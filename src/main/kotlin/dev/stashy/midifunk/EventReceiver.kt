package dev.stashy.midifunk

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import javax.sound.midi.MidiDevice
import javax.sound.midi.MidiMessage
import javax.sound.midi.Receiver

val MidiDevice.from: PublishSubject<MidiEvent>
    get() {
        (this.transmitter.receiver as? EventReceiver)?.bus?.let {
            debug("Returning EventReceiver")
            return it
        }
        debug("Creating new EventReceiver")
        return EventReceiver(this).bus!!
    }

fun MidiDevice.to(e: MidiEvent) {
    receiver.send(e.convert(), e.timestamp)
}

private class EventReceiver(dev: MidiDevice) : Receiver {
    var bus: PublishSubject<MidiEvent>? = PublishSubject.create()
    val isClosed: Boolean = bus == null

    init {
        debug("Opening device '${dev.deviceInfo.name}'")
        dev.open()
        dev.transmitter.receiver = this
    }

    override fun close() {
        bus = null
    }

    override fun send(message: MidiMessage?, timeStamp: Long) {
        if (!isClosed)
            message?.let {
                MidiEvent.convert(message.message.mapTo(mutableListOf()) { it.toInt() }, timeStamp).let {
                    bus!!.publish { Observable.just(it) }.publish()
                }
            }
    }
}

