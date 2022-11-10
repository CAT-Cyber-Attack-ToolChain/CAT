package com.attackAgent
class RandomAttackAgent : AttackAgent() {

    override fun attack() {
        var currentNode: Int = adapter.getStartNode()
        visitedNode.add(currentNode)

        var connectedNodes = adapter.getConnectedNodes(currentNode)

        while (connectedNodes.isNotEmpty()) {
            currentNode = connectedNodes.random()
            connectedNodes = adapter.getConnectedNodes(currentNode)
            visitedNode.add(currentNode)
        }
    }
}

fun main(args : Array<String>) {

    val random : AttackAgent = RandomAttackAgent()
    random.attack()
    random.returnPath()
}
