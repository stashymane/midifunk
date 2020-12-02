package dev.stashy.midifunk

import java.text.SimpleDateFormat
import java.util.*

inline val Int.msb: Int
    get() = this and 0xF0 shr 4

fun Int.withMsb(b: Int): Int {
    return (b and 0xF shl 4) + (this and 0xF)
}

inline val Int.lsb: Int
    get() = this and 0xF

fun Int.withLsb(b: Int): Int {
    return (b and 0xF) + (this and 0xF0)
}

private var devMode: Boolean = System.getenv("midifunk_dev").equals("true", true)
private val dateFormat = SimpleDateFormat("HH:mm:ss")

fun debug(msg: String) {
    if (devMode)
        println("${dateFormat.format(Date())} $msg")
}