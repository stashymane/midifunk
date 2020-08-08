package dev.stashy.midibind.midi

val Int.msb: Int
    get() = this and 0xF0 shr 4

fun Int.withMsb(b: Int): Int {
    return (b and 0xF shl 4) + (this and 0xF)
}

val Int.lsb: Int
    get() = this and 0xF

fun Int.withLsb(b: Int): Int {
    return (b and 0xF) + (this and 0xF0)
}