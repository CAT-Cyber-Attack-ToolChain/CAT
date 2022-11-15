package com.attackAgent

import com.neo4j.Node
import com.neo4j.Rule

class RealAttackAgent : AttackAgent() {
    override fun attack() {
        val startNode = adapter.getGraph()
        println(startNode.connections)
        var node = startNode

        while (!node.connections.isEmpty()) {
            var rule = chooseRule(node)
            path.add(RuleNodePair(node, rule))
            val index = node.connections[rule]!!
            node = adapter.nodes[index]!!
        }
    }

    private fun chooseRule(n : Node): Rule {
        var pickedRule: Rule? = null
        n.connections.forEach {entry ->
            val rule: Rule = entry.key
            rule.calculateEasynessScore()
            if (pickedRule == null) {
                pickedRule = rule
            } else if (pickedRule!!.easyness > rule.easyness)
                pickedRule = rule
        }
        return pickedRule!!
    }

}