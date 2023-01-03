package com.attackAgent

import com.graph.Node
import com.graph.Rule

class RealAttackAgent : AttackAgent() {

    override fun chooseRule(n : Node): Rule {
        var pickedRule: Rule? = null
        n.getConnections().forEach {rule ->
            rule.calculateEasinessScore()
            if (pickedRule == null) {
                pickedRule = rule
            } else if (pickedRule!!.easiness > rule.easiness)
                pickedRule = rule
        }
        return pickedRule!!
    }

}