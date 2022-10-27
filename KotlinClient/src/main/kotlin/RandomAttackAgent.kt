import contoller.Neo4JController
import org.neo4j.driver.AuthTokens
import org.neo4j.driver.Driver
import org.neo4j.driver.GraphDatabase
import org.neo4j.driver.Session
import org.neo4j.driver.Values.parameters;

import kotlin.random.Random

class RandomAttackAgent {

    private val driver: Driver = Neo4JController.driver

    private fun getRandomNode(): Int {
        val session: Session = driver.session()

        val maxId: Int = session.writeTransaction { tx ->
            val result: org.neo4j.driver.Result = tx.run("MATCH(n) RETURN MAX (n.node_id)", parameters())
            result.list()[0].get(0).toString().toInt()
        }

        return Random.nextInt(0, maxId) + 1
    }

    private fun getConnectedNodes(id: Int): List<Int> {
        val session: Session = driver.session()

        val connectedNodeIds: List<Int> = session.writeTransaction { tx ->
            val result: org.neo4j.driver.Result =
                tx.run("MATCH(start {node_id: ${id}})-[:To]->(end) RETURN (end.node_id)", parameters())
            result.list().map { e -> e[0].toString().toInt() }
        }

        return connectedNodeIds
    }

    fun attack() {
        val path: MutableList<Int> = mutableListOf()

        var currentNode: Int = getRandomNode()
        path.add(currentNode)

        var connectedNodes = getConnectedNodes(currentNode)

        while (connectedNodes.isNotEmpty()) {
            currentNode = connectedNodes.random()
            connectedNodes = getConnectedNodes(currentNode)
            path.add(currentNode)
        }

        printPath(path)
    }

    fun printPath(path: List<Int>) {
        for (id in path) {
            val session: Session = driver.session()

            val text: String = session.writeTransaction { tx ->
                val result: org.neo4j.driver.Result = tx.run("MATCH(n {node_id: ${id}}) RETURN (n.text)", parameters())
                result.list().toString()
            }
            println(text)
        }
    }
}

fun main(args: Array<String>) {

    val randomAttackAgent: RandomAttackAgent = RandomAttackAgent()
    randomAttackAgent.attack()

}