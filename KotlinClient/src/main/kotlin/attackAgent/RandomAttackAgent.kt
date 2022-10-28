package attackAgent

import neo4j.Neo4JAdapter
import org.neo4j.driver.AuthTokens
import org.neo4j.driver.Driver
import org.neo4j.driver.GraphDatabase
import org.neo4j.driver.Session
import org.neo4j.driver.Values.parameters;

import kotlin.random.Random

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
