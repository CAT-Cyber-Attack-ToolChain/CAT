import java.io.StringReader

import org.neo4j.driver.AuthTokens
import org.neo4j.driver.Driver
import org.neo4j.driver.GraphDatabase
import org.neo4j.driver.Session
import org.neo4j.driver.Values.parameters
import org.neo4j.driver.Result
import org.neo4j.driver.Record

import com.beust.klaxon.Klaxon
import com.beust.klaxon.JsonReader
import com.beust.klaxon.JsonObject
import com.beust.klaxon.JsonArray

class Export {
    companion object {
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

        fun translateToCytoscapeJS(json: String) {

            val klaxon = Klaxon()
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
                            var start: Node? = null
                            var end: Node? = null

                            while (reader.hasNext()) {
                                val readName = reader.nextName()
                                when (readName) {
                                    "type" -> type = reader.nextString()
                                    "id" -> id = reader.nextString()
                                    "labels" -> labels =
                                        reader.nextArray().map { a -> a.toString() }
                                    "properties" -> properties = reader.nextObject()
                                    "label" -> label = reader.nextString()
                                    "start" -> reader.beginObject() {
                                        var startId: String = ""
                                        var startLabels: List<String> = arrayListOf<String>()
                                        var startProperties: JsonObject? = null
                                        while (reader.hasNext()) {
                                            val startName = reader.nextName()
                                            when (startName) {
                                                "id" -> startId = reader.nextString()
                                                "labels" -> startLabels = reader.nextArray().map {a -> a.toString()}
                                                "properties" -> startProperties = reader.nextObject()
                                                else -> println("Miss")
                                            }
                                        }
                                        start = Node(startId, startLabels, startProperties)
                                    }
                                    "end" -> reader.beginObject() {
                                        var endId: String = ""
                                        var endLabels: List<String> = arrayListOf<String>()
                                        var endProperties: JsonObject? = null
                                        while (reader.hasNext()) {
                                            val endName = reader.nextName()
                                            when (endName) {
                                                "id" -> endId = reader.nextString()
                                                "labels" -> endLabels = reader.nextArray().map {a -> a.toString()}
                                                "properties" -> endProperties = reader.nextObject()
                                                else -> println("Miss")
                                            }
                                        }
                                        end = Node(endId, endLabels, endProperties)
                                    }
                                    else -> println("Miss")
                                }
                            }
                            var nr : NodeOrRelationship =
                            when (type) {
                                "node" -> GraphNode(id.toInt(), properties as Map<String, Any>, labels)
                                "relationship" -> Relationship(id.toInt(), properties as Map<String, Any>, label, start!!.id.toInt(), end!!.id.toInt())
                                else -> throw NoSuchElementException("Only Nodes and Relationships accepted")
                            }
                        }
                    }
                }
            }
        }
    }
}

data class Node(
    var id: String,
    var labels: List<String>,
    var properties: JsonObject?
)

open class NodeOrRelationship(open val id: Int, open val properties: Map<String, Any>)

data class GraphNode(override val id: Int, override val properties: Map<String, Any>, val labels: List<String>) : NodeOrRelationship(id, properties)

data class Relationship(override val id: Int, override val properties: Map<String, Any>, val label: String, val startId: Int, val endId: Int) : NodeOrRelationship(id, properties)
