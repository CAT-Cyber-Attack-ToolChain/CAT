package com.plugins

import com.attackAgent.RealAttackAgent
import com.beust.klaxon.Klaxon
import com.controller.MulvalController
import com.controller.Neo4JController
import com.cytoscape.CytoDataWrapper
import com.cytoscape.CytoEdge
import com.cytoscape.CytoNode
import com.example.model.PathCache
import com.graph.TopologyGraph
import com.model.AttackGraphOutput
import com.model.MulvalInput
import com.model.Neo4JMapping
import com.neo4j.Neo4JAdapter
import com.neo4j.Node
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File
import java.util.*

fun Route.GraphGenRouting() {
    val cur = System.getProperty("user.dir") // cur = backend directory
    var filePath: String = "";

    fun generateGraph(mulvalController: MulvalController, neo4JController: Neo4JController): String {
        // upload the graph to Neo4j
        if (mulvalController.generateGraph()) {
            neo4JController.update()
        }
        // get the graph data from Neo4j
        return exportToCytoscapeJSON()
    }
    // get the graph data from Neo4j

    return exportToCytoscapeJSON()
  }

  route("/test") {
    get {
      val attack = RealAttackAgent()
//            val attack = RandomAttackAgent()
      attack.attack()
      attack.printPath()
    }
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

    route("/submitInput") {
        post {
            val upload = call.receive<TopologyInput>()
            val mulvalInput = MulvalInput("$cur/input.P")
            val mulvalOutput = AttackGraphOutput("$cur/../../output")
            TopologyGraph.build(upload.machines, upload.routers, upload.links, "$cur/input.P")
            // generate the graph, move to Neo4j, and display it on frontend
            val neo4JController = Neo4JController(mulvalOutput, PathCache("$cur/input.P"), "default")
            Neo4JMapping.add(neo4JController)
            val attackGraphJson = generateGraph(MulvalController(mulvalInput, mulvalOutput), neo4JController)
            println(attackGraphJson)
            call.respond("{\"attackGraph\": $attackGraphJson}")
        }
      }
      // generate the graph, move to Neo4j, and display it on frontend
      val neo4JController = Neo4JController(mulvalOutput, PathCache(filePath), "default")
      Neo4JMapping.add(neo4JController)
      val attackGraphJson = generateGraph(MulvalController(mulvalInput, mulvalOutput), neo4JController)
      TopologyGraph.topologyGraph = TopologyGraph.build(mulvalInput)
      val topologyGraphJson = TopologyGraph.topologyGraph!!.exportToCytoscapeJSON()
      println(attackGraphJson)
      call.respond("{\"attackGraph\": $attackGraphJson, \"topologyGraph\": $topologyGraphJson}")
    }
  }
}

@kotlinx.serialization.Serializable
data class TopologyInput(val machines: String, val routers: String, val links: String)


fun nodeToCytoJSON(n: Node): List<CytoDataWrapper> {
  val result: LinkedList<CytoDataWrapper> = LinkedList()
  result.add(CytoDataWrapper(CytoNode("n${n.getId()}", n.getPermission())))
  n.getConnections().forEach { rule -> result.add(CytoDataWrapper(CytoEdge("e${rule.getId()}", "n${n.getId()}", "n${rule.getDest().getId()}", rule.getText()))) }
  return result
}

fun exportToCytoscapeJSON(): String {
  val klaxon = Klaxon()
  val adapter = Neo4JAdapter()
  val nodestrlist: List<String> = adapter.nodes.values.map { n ->

    val dataWrappers = nodeToCytoJSON(n)
    val nodeStrs = dataWrappers.map { dw -> klaxon.toJsonString(dw) }
    nodeStrs.joinToString()
  }

  return "[" + nodestrlist.joinToString() + "]"
}