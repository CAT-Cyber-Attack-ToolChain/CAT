import org.neo4j.driver.AuthTokens
import org.neo4j.driver.Driver
import org.neo4j.driver.GraphDatabase
import org.neo4j.driver.Session
import org.neo4j.driver.Values.parameters
import org.neo4j.driver.Result
import org.neo4j.driver.Record

class Export {
    companion object {
        val driver: Driver = GraphDatabase.driver("neo4j+s://42ce3f9a.databases.neo4j.io", AuthTokens.basic("neo4j", "qufvn4LK6AiPaRBIWDLPRzFh4wqzgI5x_n2bXHc1d38"))

        fun close() {
            driver.close()
        }

        fun exportToJSON() {
            val session: Session = driver.session()
            val result: String = session.writeTransaction { tx ->
                val result: org.neo4j.driver.Result = tx.run("CALL apoc.export.json.all(null,{useTypes:true,stream: true,jsonFormat:'ARRAY_JSON'})\n" +
                        "YIELD file, nodes, relationships, properties, data\n" +
                        "RETURN file, nodes, relationships, properties, data", parameters())
                result.single().get("data").toString()
            }
            println(result)
        }

        fun translateToCytoscapeJS(json: String) {
            // TODO: translate json to something cytoscape can use
        }
    }
}