package metrics
abstract class DecisionMetric : Metric {
    abstract override fun calculate(): Int
}