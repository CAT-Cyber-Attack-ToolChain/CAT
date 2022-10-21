package metrics.decision

import metrics.DecisionMetric
import org.neo4j.driver.*

class ShortestPath : DecisionMetric() {
    override fun calculate(): Int {
        //1. find the destination node D
        //2. find all the neighbours and add them to an and set and an or set. 
        //3. for each member of the OR set O, create a new node with O appended
        //   to the AND set.

        val session: Session = driver.session()
        val result: List<Int> = session.writeTransaction { tx ->
            val result: org.neo4j.driver.Result = tx.run("MATCH(a: Permission {node_id: 18}), (b: Permission {node_id: 1}), p=(a)-[*]->(b) RETURN p", Values.parameters())
            result.list {r -> r.get(0).size()}
        }
        return result.min()
    }

    companion object{
        val driver: Driver = GraphDatabase.driver("neo4j+s://42ce3f9a.databases.neo4j.io", AuthTokens.basic("neo4j", "qufvn4LK6AiPaRBIWDLPRzFh4wqzgI5x_n2bXHc1d38"))
    }
}
