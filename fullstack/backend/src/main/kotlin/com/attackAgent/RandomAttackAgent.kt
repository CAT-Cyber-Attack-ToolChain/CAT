package com.attackAgent

import com.graph.Node
import com.graph.Rule

class RandomAttackAgent : AttackAgent() {

    override fun chooseRule(n: Node): Rule {
        return n.getConnections().random()
    }
}

fun main(args : Array<String>) {
    val random : AttackAgent = RandomAttackAgent()
    random.attack()
    print(random.returnPath())
}
