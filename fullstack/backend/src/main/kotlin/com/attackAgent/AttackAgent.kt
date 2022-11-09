package com.attackAgent

import com.neo4j.Neo4JAdapter
import com.neo4j.Node
import com.neo4j.Rule

open class AttackAgent {

    protected val path: MutableList<RuleNodePair> = mutableListOf()
    protected val adapter: Neo4JAdapter = Neo4JAdapter()

    open fun attack() {}

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