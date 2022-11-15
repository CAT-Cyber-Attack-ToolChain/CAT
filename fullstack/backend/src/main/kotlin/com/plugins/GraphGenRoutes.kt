package com.plugins

import com.Export
import com.controller.MulvalController
import com.controller.Neo4JController
import com.graph.Graph
import com.model.AttackGraphOutput
import com.model.MulvalInput
import com.model.Neo4JMapping
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.content.*
import com.example.model.PathCache
import com.graph.AttackGraph
import com.graph.AttackNode
import java.io.File


fun Route.GraphGenRouting() {
    val example = """[{ "data": { "id": "one", "label": "Node 1" }}, 
        { "data": { "id": "two", "label": "Node 2" }},
        { "data": { "source": "one", "target": "two", "label": "Edge from Node1 to Node2" } }]"""

    val cur = System.getProperty("user.dir") // cur = backend directory
    var filePath: String = "";

    fun generateGraph(mulvalController: MulvalController, neo4JController: Neo4JController): String {
        // upload the graph to Neo4j
        if (mulvalController.generateGraph()) {
            neo4JController.update()
        }
        // get the graph data from Neo4j
        val graph: AttackGraph = Export.translateToGraph(Export.exportToJSON())
        return graph.exportToCytoscapeJSON()
    }

    route("/submitInput") {
        post {
            val upload = call.receiveMultipart()
            lateinit var mulvalInput: MulvalInput
            lateinit var mulvalOutput: AttackGraphOutput
            upload.forEachPart { part ->
                if (part is PartData.FileItem) {
                    // retrieve file name of upload
                    val name = part.originalFileName!!
                    filePath = "$cur/src/main/resources/uploads/$name"
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

                    return@forEachPart
                }
            }
            // generate the graph, move to Neo4j, and display it on frontend
            val neo4JController = Neo4JController(mulvalOutput, PathCache(filePath), "default")
            Neo4JMapping.add(neo4JController)
            val cytoscapeJson = generateGraph(MulvalController(mulvalInput, mulvalOutput), neo4JController)
            call.respond(cytoscapeJson)
        }
    }
}
