package com.ktor.routes

import com.attackAgent.RealAttackAgent
import com.controller.Mulval
import com.controller.Neo4J
import com.controller.Metric
import com.graph.AttackGraph
import com.graph.TopologyGraph
import com.model.PathCache
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Route.GraphGenRouting() {
	val cur = System.getProperty("user.dir") // cur = backend directory
	val attackGraph = AttackGraph()
  Neo4J.addObserver(attackGraph)

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
			Mulval.generateGraph()
			val attackGraphJson = attackGraph.exportToCytoscapeJSON()
			println(attackGraphJson)
			
			val metrics = Metric.getMetrics()

			call.respond("{\"attackGraph\": $attackGraphJson, \"reachability\": $reachability}")
			
			
		}
	}
}

@kotlinx.serialization.Serializable
data class TopologyInput(val machines: String, val routers: String, val links: String)
