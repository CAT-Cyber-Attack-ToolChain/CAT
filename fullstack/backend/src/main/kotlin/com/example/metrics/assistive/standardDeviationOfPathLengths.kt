package metrics.assistive

import metrics.AssistiveMetric
import model.PathCache
import kotlin.math.pow
import kotlin.math.sqrt

class StandardDeviationOfPathLengths(private val cache: PathCache) : AssistiveMetric() {
    override fun toString(): String {
        return "Standard Deviation of Path Lengths"
    }
    override fun calculate(): Double {
        val lst = cache.get()
        val mean = lst.average()
        return sqrt(lst.fold(0.0) { accumulator, next -> accumulator + (next - mean).pow(2.0) } /lst.size)
    }
}
