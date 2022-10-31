package com.example.attackAgent


import com.example.controller.Neo4JController.Companion.driver
import com.example.neo4j.Neo4JAdapter
import org.neo4j.driver.Session
import org.neo4j.driver.Values

open class AttackAgent {

    protected val path: MutableList<Int> = mutableListOf()
    protected val adapter : Neo4JAdapter = Neo4JAdapter()

    open fun attack() {}

    fun printPath() {
        for (id in path) {
            val session: Session = driver.session()

            val text: String = session.writeTransaction { tx ->
                val result: org.neo4j.driver.Result = tx.run(
                    "MATCH(n {node_id: ${id}}) RETURN (n.text)",
                    Values.parameters()
                )
                result.list().toString()
            }
            println(text)
        }
    }
}
