
import com.metrics.AssistiveMetric
import model.PathCache

class ModeOfPathLengths(private val cache: PathCache) : AssistiveMetric() {

    override fun toString(): String {
        return "Mode of Path Lengths"
    }

    override fun calculate(): Double {
        val lst = cache.get()
        var mode : Int = lst[0]
        var best = 0
        var start = 0
        var current = lst[0]
        for (i in lst.indices) {
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
