package com.attackAgent

import com.neo4j.Node
import com.neo4j.Rule

class RandomAttackAgent : AttackAgent() {

    override fun attack() {
        var currentNode: Node = adapter.getGraph()
        var rules : Set<Rule> = currentNode.connections.keys

        while (rules.isNotEmpty()) {
            val rule = rules.random()
            currentNode = currentNode.connections.get(rule)!!
            rules = currentNode.connections.keys
            path.add(RuleNodePair(currentNode, rule))
        }
    }
}

fun main(args : Array<String>) {
    val random : AttackAgent = RandomAttackAgent()
    random.attack()
    random.printPath()
}
