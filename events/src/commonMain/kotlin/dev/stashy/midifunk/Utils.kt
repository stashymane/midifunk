package dev.stashy.midifunk

inline val UInt.msb: UInt
    get() = this and 240u shr 4

fun UInt.withMsb(b: UInt): UInt {
    return (b and 15u shl 4) + (this and 15u)
}

inline val UInt.lsb: UInt
    get() = this and 15u

fun UInt.withLsb(b: UInt): UInt {
    return (b and 15u) + (this and 240u)
}
