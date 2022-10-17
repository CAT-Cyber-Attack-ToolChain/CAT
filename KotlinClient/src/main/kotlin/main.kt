import org.neo4j.driver.AuthTokens
import org.neo4j.driver.Driver
import org.neo4j.driver.GraphDatabase
import org.neo4j.driver.Session
import org.neo4j.driver.Values.parameters;

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

fun main() {
    println("Hello, World!")
    get_graph()
    close()
}
