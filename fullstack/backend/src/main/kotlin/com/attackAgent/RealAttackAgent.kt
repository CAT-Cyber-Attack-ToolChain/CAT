package com.attackAgent

import com.neo4j.Node
import com.neo4j.Rule

class RealAttackAgent : AttackAgent() {

    override fun chooseRule(n : Node): Rule {
        var pickedRule: Rule? = null
        n.connections.forEach {rule ->
            rule.calculateEasinessScore()
            if (pickedRule == null) {
                pickedRule = rule
            } else if (pickedRule!!.easiness > rule.easiness)
                pickedRule = rule
        }
        return pickedRule!!
    }

}