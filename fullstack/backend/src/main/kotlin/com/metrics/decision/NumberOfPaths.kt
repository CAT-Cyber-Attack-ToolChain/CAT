package com.metrics.decision

import com.metrics.DecisionMetric
import com.model.PathCache

class NumberOfPaths(private val cache: PathCache) : DecisionMetric() {
    override fun toString(): String {
        return "Number of Paths"
    }
    override fun calculate(): Double  = cache.get().size.toDouble()
}
