package net.fredrikmeyer

data class Interval(val min: Float, val max: Float) : Comparable<Interval> {
    fun contains(t: Float) = t in min..max

    override fun compareTo(other: Interval): Int {
        return min.compareTo(other.min)
    }
}
