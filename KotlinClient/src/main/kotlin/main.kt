import org.neo4j.driver.AuthTokens
import org.neo4j.driver.Driver
import org.neo4j.driver.GraphDatabase
import org.neo4j.driver.Session
import org.neo4j.driver.Values.parameters;
import kotlin.random.Random

val driver: Driver = GraphDatabase.driver("neo4j+s://42ce3f9a.databases.neo4j.io", AuthTokens.basic("neo4j", "qufvn4LK6AiPaRBIWDLPRzFh4wqzgI5x_n2bXHc1d38"))

fun close() {
    driver.close()
}

fun get_graph() {
    val session: Session = driver.session()
    val result: List<String> = session.writeTransaction { tx ->
        val result: org.neo4j.driver.Result = tx.run("MATCH(n) RETURN n", parameters())
        result.list {r -> r.toString()}
    }
    println(result)
}

fun getRandomNode(): org.neo4j.driver.Record {
    val session: Session = driver.session()

    val maxId: Int = session.writeTransaction { tx ->
        val result: org.neo4j.driver.Result = tx.run("MATCH(n) RETURN MAX (n.node_id)", parameters())
        result.list()[0].get(0).toString().toInt()
    }

    val randInt: Int = Random.nextInt(0,maxId) + 1

    val result: org.neo4j.driver.Record = session.writeTransaction { tx ->
        val result: org.neo4j.driver.Result = tx.run("MATCH(n {node_id: ${randInt}}) RETURN n", parameters())
        result.list()[0]
    }
    return result
}

fun randomAttackAgent() {
    val startNode: org.neo4j.driver.Record = getRandomNode()
}

fun main(args: Array<String>) {
    getRandomNode()
    close()
}
