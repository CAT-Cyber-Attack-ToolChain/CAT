package attackAgent

import com.attackAgent.AttackAgent

class RandomAttackAgent : AttackAgent() {

    override fun attack() {
        var currentNode: Int = adapter.getStartNode()
        path.add(currentNode)

        var connectedNodes = adapter.getConnectedNodes(currentNode)

        while (connectedNodes.isNotEmpty()) {
            currentNode = connectedNodes.random()
            connectedNodes = adapter.getConnectedNodes(currentNode)
            path.add(currentNode)
        }
    }
}

fun main(args : Array<String>) {
    val random : AttackAgent = RandomAttackAgent()
    random.attack()
    random.printPath()
}
