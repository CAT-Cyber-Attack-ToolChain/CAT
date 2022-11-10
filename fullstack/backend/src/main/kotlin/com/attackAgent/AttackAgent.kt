package com.attackAgent

import com.controller.Neo4JController.Companion.driver
import com.neo4j.Neo4JAdapter
import org.neo4j.driver.Session
import org.neo4j.driver.Values

open class AttackAgent {

    protected val visitedNode: MutableList<Int> = mutableListOf()
    protected val adapter : Neo4JAdapter = Neo4JAdapter()

    open fun attack() {}

    fun printPath() {
        for (id in visitedNode) {
            val session: Session = driver.session()

            val text: String = session.writeTransaction { tx ->
                val result: org.neo4j.driver.Result = tx.run(
                    "MATCH (n) WHERE ID(n) = $id RETURN (n.text)",
                    Values.parameters()
                )
                result.list().toString()

            }
            println(text)
        }
    }

    //Converts path to a tuple
    fun returnPath(): MutableList<Pair<Int,Int>> {
        val paths = mutableListOf<Pair<Int,Int>>()
        for (i in 0 until visitedNode.size - 1) {

            paths.add(Pair(visitedNode[i], visitedNode[i+1]))
        }
        return paths
    }

}
