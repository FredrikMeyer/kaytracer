package net.fredrikmeyer

data class Interval(val min: Float, val max: Float) {
    fun contains(t: Float) = t in min..max
}
