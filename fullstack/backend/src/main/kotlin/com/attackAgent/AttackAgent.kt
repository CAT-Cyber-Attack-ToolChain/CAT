package com.attackAgent

import com.neo4j.Neo4JAdapter
import com.neo4j.Node
import com.neo4j.Rule

abstract class AttackAgent {

    protected val path: MutableList<RuleNodePair> = mutableListOf()
    protected val visitedNode: MutableList<Int> = mutableListOf()
    protected val adapter : Neo4JAdapter = Neo4JAdapter()

    fun attack() {
        val startNode = adapter.getGraph()
        var node = startNode

        while (!node.connections.isEmpty()) {
            var rule = chooseRule(node)
            path.add(RuleNodePair(node, rule))
            val index = node.connections[rule]!!
            node = adapter.nodes[index]!!
        }
    }

    protected abstract fun chooseRule(n: Node): Rule

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

    //Converts path to a pairs of string
    fun returnPath(): MutableList<Pair<String,String>> {
        val paths = mutableListOf<Pair<String,String>>()
        for (i in 0 until visitedNode.size - 1) {

            paths.add(Pair("n" + visitedNode[i], "n" + visitedNode[i+1]))
        }
        return paths
    }

}

class RuleNodePair(
    val node: Node,
    val rule: Rule
) {}