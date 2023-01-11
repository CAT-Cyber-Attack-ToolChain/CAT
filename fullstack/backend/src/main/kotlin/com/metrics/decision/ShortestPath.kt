package com.metrics.decision

import com.metrics.DecisionMetric
import com.model.PathCache

class ShortestPath(private val cache: PathCache) : DecisionMetric() {
    override fun toString(): String {
        return "Shortest Path"
    }
    override fun calculate(): Double {
        println(cache.get())
        return cache.get().min().toDouble()
    }
}

