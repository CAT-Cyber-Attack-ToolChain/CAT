package com.attackAgent

import com.neo4j.Neo4JAdapter
import com.neo4j.Node
import com.neo4j.Rule

abstract class AttackAgent {

    protected val path: MutableList<RuleNodePair> = mutableListOf()
    protected val adapter : Neo4JAdapter = Neo4JAdapter()

    fun attack() {
        val startNode = adapter.getGraph()
        var currNode = startNode

        while (currNode.connections.isNotEmpty()) {
            var rule = chooseRule(currNode)
            path.add(RuleNodePair(currNode, rule))
            currNode = rule.dest
        }
    }

    protected abstract fun chooseRule(n: Node): Rule

    fun printPath() {
        for (pair in path) {
            println(pair.rule.rule)
            println(pair.node.permission)
        }
    }


    //Converts path to a pairs of string
    fun returnPath(): MutableList<Pair<String,String>> {
        val paths = mutableListOf<Pair<String,String>>()

        for (ruleNodePair: RuleNodePair in path) {

            paths.add(Pair("n${ruleNodePair.node.id}", "n${ruleNodePair.rule.dest.id}"))
        }

        return paths
    }

}

class RuleNodePair(
    val node: Node,
    val rule: Rule
) {}