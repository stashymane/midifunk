package dev.stashy.midifunk

import io.reactivex.rxjava3.subjects.PublishSubject
import javax.sound.midi.MidiDevice
import javax.sound.midi.MidiMessage
import javax.sound.midi.Receiver

val MidiDevice.from: PublishSubject<MidiEvent>
    get() {
        (this.transmitter.receiver as? EventReceiver)?.bus?.let { return it }
        return EventReceiver(this).bus
    }

fun MidiDevice.to(e: MidiEvent) {
    receiver.send(e.convert(), e.timestamp)
}

private class EventReceiver(dev: MidiDevice) : Receiver {
    var bus: PublishSubject<MidiEvent> = PublishSubject.create()

    init {
        dev.transmitter.receiver = this
        bus.doOnSubscribe { dev.open() }?.subscribe()
    }

    override fun close() {
        bus.onComplete()
    }

    override fun send(message: MidiMessage, timeStamp: Long) {
        MidiEvent.convert(message.message.mapTo(mutableListOf()) { it.toInt() }, timeStamp).let {
            bus.onNext(it)
        }
    }
}

