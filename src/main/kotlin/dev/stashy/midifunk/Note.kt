package dev.stashy.midifunk

object Note {
    fun C(oct: Int): UInt = offset(oct)
    fun CSharp(oct: Int): UInt = offset(oct) + 1u
    fun D(oct: Int): UInt = offset(oct) + 2u
    fun DSharp(oct: Int): UInt = offset(oct) + 3u
    fun E(oct: Int): UInt = offset(oct) + 4u
    fun F(oct: Int): UInt = offset(oct) + 5u
    fun FSharp(oct: Int): UInt = offset(oct) + 6u
    fun G(oct: Int): UInt = offset(oct) + 7u
    fun GSharp(oct: Int): UInt = offset(oct) + 8u
    fun A(oct: Int): UInt = offset(oct) + 9u
    fun ASharp(oct: Int): UInt = offset(oct) + 10u
    fun B(oct: Int): UInt = offset(oct) + 11u

    private fun offset(oct: Int): UInt = (oct + 2).toUInt() * 12u
}
