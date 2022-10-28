package metrics.decision

import metrics.DecisionMetric
import metrics.assistive.MeanOfPathLengths

class NormalisedMOPL : DecisionMetric() {
  override fun calculate(): Double = MeanOfPathLengths().calculate() / NumberOfPaths().calculate()
}