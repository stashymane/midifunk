package dev.stashy.midibind.midi

val Int.msb: Int
    get() = this and 0xF0 shr 4

val Int.lsb: Int
    get() = this and 0xF