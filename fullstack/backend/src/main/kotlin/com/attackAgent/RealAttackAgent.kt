package com.attackAgent

import com.neo4j.Node
import com.neo4j.Rule

class RealAttackAgent : AttackAgent() {
    override fun attack() {
        chooseRule(adapter.getGraph())
    }

    private fun chooseRule(n : Node): Rule {
        var pickedRule: Rule? = null
        n.connections.forEach {entry -> {
            val rule = entry.key
            rule.calculateEasynessScore()
            pickedRule = rule
        }}
        return pickedRule!!
    }

}