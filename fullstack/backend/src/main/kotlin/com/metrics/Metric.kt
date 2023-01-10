package com.metrics
interface Metric<T> {
    fun calculate(): T
}