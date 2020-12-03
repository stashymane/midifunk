package dev.stashy.midifunk

import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.PublishSubject
import javax.sound.midi.*

val MidiDevice.from: PublishSubject<MidiEvent>
    get() {
        return if (this.transmitter.receiver is EventReceiver) {
            debug("Returning EventReceiver")
            (transmitter.receiver as EventReceiver).bus
        } else {
            debug("Creating new EventReceiver")
            EventReceiver(this).bus
        }
    }

fun MidiDevice.to(e: MidiEvent) {
    receiver.send(e.convert(), e.timestamp)
}

private class EventReceiver(dev: MidiDevice) : Receiver {
    var bus = PublishSubject.create<MidiEvent>()

    init {
        debug("Opening device '${dev.deviceInfo.name}'")
        dev.open()
        dev.transmitter.receiver = this
    }

    override fun close() {
        bus = null
    }

    override fun send(message: MidiMessage?, timeStamp: Long) {
        if (message == null) return
        val data = MidiEvent.convert(message.message.mapTo(mutableListOf()) { it.toInt() }, timeStamp)
        bus.publish { Observable.just(data) }.publish()
    }
}

