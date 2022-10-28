package attackAgent

import Main.Companion.driver
import org.neo4j.driver.Session
import org.neo4j.driver.Values

interface AttackAgent {

    val path: MutableList<Int>

    fun attack() {}

    fun printPath() {
        for (id in path) {
            val session: Session = driver.session()

            val text: String = session.writeTransaction { tx ->
                val result: org.neo4j.driver.Result = tx.run("MATCH(n {node_id: ${id}}) RETURN (n.text)",
                    Values.parameters()
                )
                result.list().toString() }
            println(text)
        }
    }
}