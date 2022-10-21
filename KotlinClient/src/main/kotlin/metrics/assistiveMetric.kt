package metrics

abstract class AssistiveMetric : Metric<Double> {
    abstract override fun calculate(): Double
}