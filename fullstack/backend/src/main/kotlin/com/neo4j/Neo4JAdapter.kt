package com.neo4j

import org.neo4j.driver.*
import org.neo4j.driver.Values.parameters

class Neo4JAdapter {

    private val driver: Driver = GraphDatabase.driver(
        "neo4j+s://42ce3f9a.databases.neo4j.io",
        AuthTokens.basic("neo4j", "qufvn4LK6AiPaRBIWDLPRzFh4wqzgI5x_n2bXHc1d38")
    )

    fun getStartNode(): Int {
        val session: Session = driver.session()
        return session.writeTransaction { tx ->
            val result: Result = tx.run(
                "MATCH(n) WHERE n.text STARTS WITH \"attackerLocated\" RETURN ID(n)", parameters()
            )
            result.list()[0].get(0).toString().toInt()
        }
    }

    fun getConnectedNodes(id: Int): List<Int> {
        val session: Session = driver.session()
        val connectedNodeIds: List<Int> = session.writeTransaction { tx ->
            val result: Result =
                tx.run("MATCH(start)-[:To]->(end) WHERE ID(start) = $id RETURN ID(end)", parameters("id", id))
            result.list().map { e -> e[0].toString().toInt() }
        }
        return connectedNodeIds
    }
}
