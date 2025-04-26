package net.fredrikmeyer

data class Interval(val min: Float, val max: Float) : Comparable<Interval>,
    ClosedFloatingPointRange<Float> {
    override val start: Float = min
    override val endInclusive: Float = max

    override fun contains(value: Float): Boolean {
        return value >= min && value <= max
    }

    override fun lessThanOrEquals(a: Float, b: Float): Boolean {
        return a <= b
    }

    override fun compareTo(other: Interval): Int {
        return min.compareTo(other.min)
    }
}
