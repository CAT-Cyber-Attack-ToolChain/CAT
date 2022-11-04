package com

import com.beust.klaxon.JsonObject
import com.beust.klaxon.JsonReader
import com.controller.Neo4JController
import com.graph.Graph
import com.graph.Node
import com.graph.NodeOrRelationship
import com.graph.Relationship
import org.neo4j.driver.Session
import org.neo4j.driver.Values.parameters
import java.io.StringReader

class Export {
  companion object {

    data class JNode(
            var id: String,
            var labels: List<String>,
            var properties: JsonObject?
    )

    private val driver = Neo4JController.driver

    fun exportToJSON(): String {
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
      driver.close()

      return trimmed

    }

    fun translateToGraph(json: String): Graph {
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
              val nodeInRelationship: () -> JNode? = {
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

              var nr: NodeOrRelationship =
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

