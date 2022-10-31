package metrics.assistive

import metrics.AssistiveMetric;
import metrics.PathCache
import kotlin.io.path.Path

class MedianOfPathLengths : AssistiveMetric() {
    override fun calculate(): Double {
        val lst = PathCache.get()
        return if (lst.size % 2 == 0) {
            (lst[lst.size / 2] + lst[(lst.size - 1) / 2]) / 2.0
        } else {
            lst[lst.size / 2].toDouble()
        }
    }
}