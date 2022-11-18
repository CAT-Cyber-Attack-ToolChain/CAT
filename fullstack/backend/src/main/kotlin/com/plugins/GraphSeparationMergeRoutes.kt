package com.plugins

import com.beust.klaxon.JsonReader
import com.controller.MulvalController
import com.controller.Neo4JController
import com.example.model.PathCache
import com.graph.TopologyGraph
import com.model.AttackGraphOutput
import com.model.MulvalInput
import com.model.Neo4JMapping
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import java.io.FileWriter
import java.io.StringReader

@Serializable
class SeparateResponse(val nodes: Set<String>, val edges: Set<Map<String, String>>)

fun Route.GraphSeparationMergeRoutes() {
    fun generateGraph(mulvalController: MulvalController, neo4JController: Neo4JController): String {
        // upload the graph to Neo4j
        if (mulvalController.generateGraph()) {
            neo4JController.update()
        }
        // get the graph data from Neo4j

        return exportToCytoscapeJSON()
    }

    route("/graph/separate") {
        post {
            val response = call.receive<SeparateResponse>()
            topologyGraph!!.separateGraph(response.nodes.map { x -> x.drop(1).toInt() }.toList(), response.edges.map { m -> m.map { e -> Pair(e.key.drop(1).toInt(), e.value.drop(1).toInt()) } }.flatten())
            val writer = FileWriter("newInput.P")
            writer.write(topologyGraph.toString())
            writer.close()
            val neo4JController = Neo4JController(AttackGraphOutput("./../../output"), PathCache("newInput.P"), "default")
            Neo4JMapping.add(neo4JController)
            val attackGraphJson = generateGraph(MulvalController(MulvalInput("newInput.P"), AttackGraphOutput("./../../output")), neo4JController)
            val topologyGraphJson = topologyGraph!!.exportToCytoscapeJSON()
            println(attackGraphJson)
            call.respond("{\"attackGraph\": $attackGraphJson, \"topologyGraph\": $topologyGraphJson}")
        }
    }
}


private fun <T> toKotlinList(jsonList: String, extract: (reader: JsonReader) -> T): List<T> {
    val list = mutableListOf<T>()
    JsonReader(StringReader(jsonList)).use {
        reader -> reader.beginArray {
            while (reader.hasNext()) {
                list.add(extract(reader))
            }
        }
    }
    return list
}

private fun nodeExtract(reader: JsonReader): Int {
    val nId = reader.nextString()
    return nId.drop(1).toInt()
}

private fun edgeExtract(reader: JsonReader): Pair<Int, Int> {
    var sourceStr = ""
    var targetStr = ""
    reader.beginObject {
        while(reader.hasNext()) {
            val next = reader.nextName()
            when (next) {
                "source" -> sourceStr = reader.nextString()
                "target" -> targetStr = reader.nextString()
            }
        }
    }
    return Pair(sourceStr.drop(1).toInt(), targetStr.drop(1).toInt())
}