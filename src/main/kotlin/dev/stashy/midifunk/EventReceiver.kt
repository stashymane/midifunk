package dev.stashy.midifunk

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import javax.sound.midi.MidiDevice
import javax.sound.midi.MidiMessage
import javax.sound.midi.Receiver

val MidiDevice.from: Observable<MidiEvent>
    get() {
        (this.transmitter.receiver as? EventReceiver)?.observable?.let { return it }
        return EventReceiver(this).observable
    }

fun MidiDevice.to(e: MidiEvent) {
    receiver.send(e.convert(), e.timestamp)
}

class EventReceiver(dev: MidiDevice, setReceiver: Boolean = true) : Receiver {
    private var bus: PublishSubject<MidiEvent> = PublishSubject.create()
    val observable: Observable<MidiEvent> = bus.doOnSubscribe { println("opened"); dev.open() }.onTerminateDetach()

    init {
        if (setReceiver)
            dev.transmitter.receiver = this
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

