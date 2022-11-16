package com.attackAgent

import com.neo4j.Neo4JAdapter
import com.neo4j.Node
import com.neo4j.Rule

abstract class AttackAgent {

    protected val path: MutableList<RuleNodePair> = mutableListOf()
    protected val adapter: Neo4JAdapter = Neo4JAdapter()

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
        for (pair in path) {
            println(pair.rule.rule)
            println(pair.node.permission)
        }
    }

    fun returnPath(): MutableList<RuleNodePair> {
        return path;
    }

}

class RuleNodePair(
    val node: Node,
    val rule: Rule
) {}