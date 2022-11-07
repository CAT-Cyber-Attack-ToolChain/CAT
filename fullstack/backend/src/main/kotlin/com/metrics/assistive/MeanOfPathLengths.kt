package metrics.assistive

import com.metrics.AssistiveMetric
import model.PathCache

class MeanOfPathLengths(private val cache: PathCache) : AssistiveMetric() {

    override fun toString(): String {
        return "Mean Path Length"
    }
    override fun calculate(): Double = cache.get().average()
}
