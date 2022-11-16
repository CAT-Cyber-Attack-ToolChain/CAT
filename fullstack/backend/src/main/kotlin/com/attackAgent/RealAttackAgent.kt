package com.attackAgent

import com.neo4j.Node
import com.neo4j.Rule

class RealAttackAgent : AttackAgent() {

    override fun chooseRule(n : Node): Rule {
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