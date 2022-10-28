package metrics.assistive

import metrics.AssistiveMetric
import metrics.PathCache
import kotlin.math.pow
import kotlin.math.sqrt

class StandardDeviationOfPathLengths : AssistiveMetric() {
    override fun calculate(): Double {
        val lst = PathCache.get()
        val mean = lst.average();
        return sqrt(lst.fold(0.0) { accumulator, next -> accumulator + (next - mean).pow(2.0) } /lst.size)
    }
}