package com.example.plugins

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import com.example.model.*
import com.example.controller.*
import com.example.graph.*
import io.ktor.http.content.*
import java.io.File


fun Route.GraphGenRouting() {
    val example = """[{ "data": { "id": "one", "label": "Node 1" }}, 
        { "data": { "id": "two", "label": "Node 2" }},
        { "data": { "source": "one", "target": "two", "label": "Edge from Node1 to Node2" } }]"""

    val cur = System.getProperty("user.dir") // cur = backend directory
    var mulvalInput = MulvalInput("$cur/../../mulval/testcases/3host/input.P")
    var mulvalOutput = AttackGraphOutput("$cur/../../output")
    println("OUTPUT_PATH: $cur/../../output")

    var mulvalController = MulvalController(mulvalInput, mulvalOutput)

    fun generateGraph(): String {
        // generate the attack graph using mulVAL
        mulvalController.generateGraph()
        val neo4JController = Neo4JController(mulvalOutput)
        // upload the graph to Neo4j
        if (mulvalController.getGenerated()) {
            neo4JController.update()
        }
        // get the graph data from Neo4j
        val graph: Graph = Export.translateToGraph(Export.exportToJSON())
        val cytoscapeJson = graph.exportToCytoscapeJSON()
        return cytoscapeJson
    }

    route("/submitInput") {
        post {
            val upload = call.receiveMultipart()
            upload.forEachPart { part ->
                if (part is PartData.FileItem) {
                    // retrieve file name of upload
                    val name = part.originalFileName!!
                    val filePath = "$cur/src/main/resources/uploads/$name"
                    val file = File(filePath)

                    // use InputStream from part to save file
                    part.streamProvider().use { its ->
                        // copy the stream to the file with buffering
                        file.outputStream().buffered().use {
                            // note that this is blocking
                            its.copyTo(it)
                        }
                    }

                    //
                    mulvalInput = MulvalInput(filePath)
                    mulvalOutput = AttackGraphOutput("$cur/../../output")

                    mulvalController = MulvalController(mulvalInput, mulvalOutput)
                    return@forEachPart
                }
            }
            // generate the graph, move to Neo4j, and display it on frontend
            val cytoscapeJson = generateGraph()
            call.respond(cytoscapeJson)
        }
    }
}
