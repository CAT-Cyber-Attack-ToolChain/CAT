package com.metrics.decision

import com.metrics.DecisionMetric
import com.example.model.PathCache

class ShortestPath(private val cache: PathCache) : DecisionMetric() {
    override fun toString(): String {
        return "Shortest Path"
    }
    override fun calculate(): Double = cache.get().min().toDouble()
}

