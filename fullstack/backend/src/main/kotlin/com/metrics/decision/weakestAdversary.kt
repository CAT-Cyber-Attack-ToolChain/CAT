package com.metrics.decision

import com.metrics.DecisionMetric
import org.neo4j.driver.*

class WeakestAdversary : DecisionMetric() {

    override fun toString(): String {
        return "Weakest Adversary"
    }
    override fun calculate() : Double {
        val session: Session = driver.session()
        val factSets: List<Int> = session.writeTransaction { tx ->
            val result: Result = tx.run("match p=(a: Fact {node_id: 18})-[*]->(b: Permission {node_id: 1}) return reduce(output=[], r in [n in nodes(p) | [(w: Fact)-[*1]->(n) | w]] | output + r)", Values.parameters())
            result.list { r -> r.get(0).size() }
        }
        return factSets.min().toDouble()
    }
    companion object {
        private val driver: Driver = GraphDatabase.driver("neo4j+s://42ce3f9a.databases.neo4j.io", AuthTokens.basic("neo4j", "qufvn4LK6AiPaRBIWDLPRzFh4wqzgI5x_n2bXHc1d38"))
    }
}
