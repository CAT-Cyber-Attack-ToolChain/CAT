package com.attackAgent

import com.neo4j.Node
import com.neo4j.Rule

class RandomAttackAgent : AttackAgent() {

    override fun chooseRule(n: Node): Rule {
        return n.connections.keys.random()
    }
}

fun main(args : Array<String>) {
    val random : AttackAgent = RandomAttackAgent()
    random.attack()
    random.returnPath()
}
