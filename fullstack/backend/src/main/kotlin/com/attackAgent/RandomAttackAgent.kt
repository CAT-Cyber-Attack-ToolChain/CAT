package com.attackAgent

import com.neo4j.Node
import com.neo4j.Rule

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
