package com.metrics.decision

import com.metrics.DecisionMetric
import com.model.PathCache
import org.neo4j.driver.*

import com.controller.Neo4J

class WeakestAdversary(private val cache: PathCache) : DecisionMetric() {

    override fun toString(): String {
        return "Weakest Adversary"
    }
    override fun calculate() : Double {
        val session: Session = driver.session()
        val factSets: MutableList<Int> = mutableListOf()
        for (goalNodeId in cache.goalNodeIds) {
            val factSetsForCurrentGoal: List<Int> = session.writeTransaction { tx ->
                val result: Result = tx.run("match p=(a: Fact {node_id: ${cache.startNodeId}})-[*]->(b: Permission {node_id: ${goalNodeId}}) return reduce(output=[], r in [n in nodes(p) | [(w: Fact)-[*1]->(n) | w]] | output + r)")
                result.list { r -> r.get(0).size() }
            }
            factSets += factSetsForCurrentGoal.toTypedArray()
        }
        return factSets.min().toDouble()
    }
    companion object {
        private val driver: Driver = Neo4J.driver!!
    }
}
