package com.metrics.assistive

import com.metrics.AssistiveMetric
import com.model.PathCache

class MedianOfPathLengths(private val cache: PathCache) : AssistiveMetric() {

    override fun toString(): String {
        return "Median Path Length"
    }
    override fun calculate(): Double {
        val lst = cache.get()
        return if (lst.size % 2 == 0) {
            (lst[lst.size / 2] + lst[(lst.size - 1) / 2]) / 2.0
        } else {
            lst[lst.size / 2].toDouble()
        }
    }
}
