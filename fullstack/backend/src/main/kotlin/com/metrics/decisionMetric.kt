package com.metrics

abstract class DecisionMetric : Metric<Double> {
    abstract override fun calculate(): Double
}