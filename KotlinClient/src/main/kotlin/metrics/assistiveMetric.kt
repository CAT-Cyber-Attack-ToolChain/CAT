package metrics

abstract class AssistiveMetric : Metric {
    abstract override fun calculate(): Int
}