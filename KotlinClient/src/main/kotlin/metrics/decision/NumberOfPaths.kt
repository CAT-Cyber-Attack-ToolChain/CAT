package metrics.decision

import metrics.DecisionMetric
import metrics.PathCache

class NumberOfPaths : DecisionMetric() {
    override fun calculate(): Double  = PathCache.get().size.toDouble()
}