package com.ktor

import com.controller.Metric
import com.controller.Mulval
import com.controller.Neo4J
import com.graph.AttackGraph

object Components {
  val attackGraph = AttackGraph()

  fun configureObjects() {
    Mulval.addObserver(Neo4J)
    Neo4J.addObserver(Metric)
    Neo4J.addObserver(attackGraph)
  }
}