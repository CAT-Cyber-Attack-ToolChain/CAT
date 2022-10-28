package metrics.assistive

import metrics.AssistiveMetric
import metrics.PathCache

class MeanOfPathLengths : AssistiveMetric() {
  override fun calculate(): Double = PathCache.get().average()
}