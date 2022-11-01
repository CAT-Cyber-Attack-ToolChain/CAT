import java.io.StringReader

import org.neo4j.driver.AuthTokens
import org.neo4j.driver.Driver
import org.neo4j.driver.GraphDatabase
import org.neo4j.driver.Session
import org.neo4j.driver.Values.parameters

import com.beust.klaxon.JsonReader
import com.beust.klaxon.JsonObject

import com.example.graph.*

class Export {
    companion object {

        data class JNode(
            var id: String,
            var labels: List<String>,
            var properties: JsonObject?
        )

        val driver: Driver = GraphDatabase.driver("neo4j+s://42ce3f9a.databases.neo4j.io", AuthTokens.basic("neo4j", "qufvn4LK6AiPaRBIWDLPRzFh4wqzgI5x_n2bXHc1d38"))

        fun close() {
            driver.close()
        }

        fun exportToJSON() : String {
            val session: Session = driver.session()
            val result: String = session.writeTransaction { tx ->
                val result: org.neo4j.driver.Result = tx.run("CALL apoc.export.json.all(null,{useTypes:true,stream: true,jsonFormat:'ARRAY_JSON'})\n" +
                        "YIELD file, nodes, relationships, properties, data\n" +
                        "RETURN file, nodes, relationships, properties, data", parameters())
                result.single().get("data").toString()
            }
            var trimmed = result.replaceFirst("\"", "")
            trimmed = trimmed.replaceAfterLast("]", "")
            trimmed = trimmed.replace("\\", "")

            return trimmed

        }

        fun translateToGraph(json: String) : Graph {
            val graph: MutableList<NodeOrRelationship> = mutableListOf()

            JsonReader(StringReader(json)).use { reader ->
                reader.beginArray() {
                    while (reader.hasNext()) {
                        reader.beginObject() {
                            var type: String = "None"
                            var id: String = "-1"
                            var labels: List<String> = arrayListOf<String>()
                            var properties: JsonObject? = null

                            // relationship
                            var label: String = "None"
                            var start: JNode? = null
                            var end: JNode? = null

                            // parse a node within a relationship (start and end)
                            val nodeInRelationship : () -> JNode? = {
                                reader.beginObject() {
                                    var startId: String = ""
                                    var startLabels: List<String> = arrayListOf<String>()
                                    var startProperties: JsonObject? = null
                                    while (reader.hasNext()) {
                                        val startName = reader.nextName()
                                        when (startName) {
                                            "id" -> startId = reader.nextString()
                                            "labels" -> startLabels =
                                                reader.nextArray().map { a -> a.toString() }
                                            "properties" -> startProperties =
                                                reader.nextObject()
                                        }
                                    }
                                    JNode(startId, startLabels, startProperties)
                                }
                            }



                            while (reader.hasNext()) {
                                val readName = reader.nextName()
                                when (readName) {
                                    "type" -> type = reader.nextString()
                                    "id" -> id = reader.nextString()
                                    "labels" -> labels =
                                        reader.nextArray().map { a -> a.toString() }
                                    "properties" -> properties = reader.nextObject()
                                    "label" -> label = reader.nextString()
                                    "start" -> start = nodeInRelationship()
                                    "end" -> end = nodeInRelationship()
                                }
                            }

                            var nr : NodeOrRelationship =
                            when (type) {
                                "node" -> Node(id.toInt(), properties as MutableMap<String, Any>, labels)
                                "relationship" -> Relationship(id.toInt(), properties as MutableMap<String, Any>, label, start!!.id.toInt(), end!!.id.toInt())
                                else -> throw NoSuchElementException("Only Nodes or Relationships can be parsed")
                            }
                            graph.add(nr)
                        }
                    }
                }
            }
            return Graph(graph)
        }
    }
}

