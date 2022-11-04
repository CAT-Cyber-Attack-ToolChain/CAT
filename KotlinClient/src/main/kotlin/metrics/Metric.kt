package metrics

interface Metric<T> {
  fun calculate(): T
}