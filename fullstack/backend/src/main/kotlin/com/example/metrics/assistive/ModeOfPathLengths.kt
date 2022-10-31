package metrics.assistive

import metrics.AssistiveMetric;
import metrics.PathCache

class ModeOfPathLengths : AssistiveMetric() {
    override fun calculate(): Double {
        val lst = PathCache.get()
        var mode : Int = lst[0]
        var best = 0
        var start = 0
        var current = lst[0]
        for (i in 0..lst.size) {
            if (lst[i] != current) {
                if (i - start > best) {
                    best = i - start
                    mode = current
                    start = i
                    current = lst[i]
                }
            }
        }
        return if (mode != current && lst.size - 1 - start > best) {
            current.toDouble()
        } else {
            mode.toDouble()
        }
    }
}