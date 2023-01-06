package com.ktor.routes

import com.attackAgent.MitreTechnique
import com.attackAgent.getMitreTechnique
import com.attackAgent.RealAttackAgent
import com.beust.klaxon.Klaxon
import com.controller.Mulval
import com.controller.Neo4J
import com.cytoscape.CytoDataWrapper
import com.cytoscape.CytoEdge
import com.cytoscape.CytoNode
import com.model.PathCache
import com.graph.TopologyGraph
import com.model.MachineExtractor
import com.graph.AttackGraph
import com.graph.Node
import com.graph.Rule
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File
import java.util.*

fun Route.GraphGenRouting() {
  val cur = System.getProperty("user.dir") // cur = backend directory

  fun generateGraph(): String {
    // upload the graph to Neo4j
    if (Mulval.generateGraph()) {
      Neo4J.update()
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
      val upload = call.receive<TopologyInput>()
      val mulvalInput = File("$cur/input.P")
      val mulvalOutput = File("$cur/../../output")
      val reachability = TopologyGraph.build(upload.machines, upload.routers, upload.links, "$cur/input.P")
      println(reachability)
      // generate the graph, move to Neo4j, and display it on frontend
      Neo4J.init(mulvalOutput, PathCache("$cur/input.P"))
      Mulval.init(mulvalInput, mulvalOutput)
      val attackGraphJson = generateGraph()
      println(attackGraphJson)
      call.respond("{\"attackGraph\": $attackGraphJson, \"reachability\": $reachability}")
    }
  }
}

@kotlinx.serialization.Serializable
data class TopologyInput(val machines: String, val routers: String, val links: String)


fun nodeToCytoJSON(n: Node): List<CytoDataWrapper> {
  val result: LinkedList<CytoDataWrapper> = LinkedList()
  val node = CytoNode("n${n.getId()}", n.getPermission())
  node.addProperty("machines", MachineExtractor.extract(n.getPermission()))
  result.add(CytoDataWrapper(node))
  n.getConnections().forEach { rule -> result.add(CytoDataWrapper(ruleToCytoEdge(rule, n))) }
  return result
}

fun ruleToCytoEdge(rule: Rule, n: Node): CytoEdge {
  val technique = getMitreTechnique(rule)
  var label = technique.technique
  if (technique == MitreTechnique.nullTechnique)
    label = rule.getText()
  return CytoEdge("e${rule.getId()}", "n${n.getId()}", "n${rule.getDest().getId()}", label)
}

fun exportToCytoscapeJSON(): String {
  val klaxon = Klaxon()
  val adapter = AttackGraph()
  val nodestrlist: List<String> = adapter.nodes.values.map { n ->

    val dataWrappers = nodeToCytoJSON(n)
    val nodeStrs = dataWrappers.map { dw -> klaxon.toJsonString(dw) }
    nodeStrs.joinToString()
  }

  return "[" + nodestrlist.joinToString() + "]"
}