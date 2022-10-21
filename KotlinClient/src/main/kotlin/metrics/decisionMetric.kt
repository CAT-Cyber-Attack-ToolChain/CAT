package metrics
abstract class DecisionMetric : Metric<Int> {
    abstract override fun calculate(): Int
}