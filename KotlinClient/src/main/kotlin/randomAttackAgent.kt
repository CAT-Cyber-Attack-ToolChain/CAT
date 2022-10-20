import org.neo4j.driver.AuthTokens
import org.neo4j.driver.Driver
import org.neo4j.driver.GraphDatabase
import org.neo4j.driver.Session
import org.neo4j.driver.Values.parameters;

import kotlin.random.Random

class randomAttackAgent {

    private val driver: Driver = GraphDatabase.driver("neo4j+s://42ce3f9a.databases.neo4j.io", AuthTokens.basic("neo4j", "qufvn4LK6AiPaRBIWDLPRzFh4wqzgI5x_n2bXHc1d38"))

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
            val result: org.neo4j.driver.Result = tx.run("MATCH(start {node_id: ${id}})-[:To]->(end) RETURN (end.node_id)", parameters())
            result.list().map { e -> e[0].toString().toInt() }
        }

        return connectedNodeIds
    }

    fun attack() {
        val startNodeId: Int = getRandomNode()
    }

}

fun main(args: Array<String>) {

    val randomAttackAgent: randomAttackAgent = randomAttackAgent()
    randomAttackAgent.attack()

}