package metrics.assistive

import metrics.AssistiveMetric
import model.PathCache

class MeanOfPathLengths(private val cache: PathCache) : AssistiveMetric() {
    override fun calculate(): Double = cache.get().average()
}