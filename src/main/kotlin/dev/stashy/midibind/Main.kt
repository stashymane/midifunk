package dev.stashy.midibind

import dev.stashy.midibind.midi.Device
import javax.sound.midi.MidiSystem

fun main() {
    val infos = MidiSystem.getMidiDeviceInfo()
    val inputs = mutableListOf<Device>()
    infos.forEachIndexed { index, info ->
        println("$index - ${info.name}")
    }
    println("Select a device...")
    val index = readLine()!!.toInt()
    val info = infos[index]
    val dev = MidiSystem.getMidiDevice(info)
    val receiver = Device(info)

    inputs += receiver
    println("${info.name} connected")
    dev.transmitters.forEach {
        it.receiver = receiver
    }
    dev.transmitter.receiver = receiver
    dev.open()
}