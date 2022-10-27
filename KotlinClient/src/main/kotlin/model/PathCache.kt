package model

import org.neo4j.driver.*

class PathCache() {
    private val driver: Driver = GraphDatabase.driver("neo4j+s://42ce3f9a.databases.neo4j.io", AuthTokens.basic("neo4j", "qufvn4LK6AiPaRBIWDLPRzFh4wqzgI5x_n2bXHc1d38"))
    private var pathLengths: List<Int> = listOf<Int>()
    fun get(): List<Int> = pathLengths
    fun update() {
        val session: Session = driver.session()
        pathLengths = session.writeTransaction { tx ->
            val result: Result = tx.run("MATCH(a: Permission {node_id: 18}), (b: Permission {node_id: 1}), p=(a)-[*]->(b) RETURN p", Values.parameters())
            result.list {r -> r.get(0).size()}
        }.sorted()
    }
}