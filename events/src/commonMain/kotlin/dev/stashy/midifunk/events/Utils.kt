package dev.stashy.midifunk.events

internal inline val UInt.msb: UInt
    get() = this and 240u shr 4

internal fun UInt.withMsb(b: UInt): UInt {
    return (b and 15u shl 4) + (this and 15u)
}

internal inline val UInt.lsb: UInt
    get() = this and 15u

internal fun UInt.withLsb(b: UInt): UInt {
    return (b and 15u) + (this and 240u)
}
