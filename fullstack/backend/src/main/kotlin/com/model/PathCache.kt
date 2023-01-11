package com.model

import org.neo4j.driver.*

import java.io.File
import java.io.IOException
import java.util.*
import com.controller.Neo4J
import com.graph.AttackGraph
import com.graph.Rule
import com.graph.Node

class PathCache(val filePath: String, val attackGraph: AttackGraph) {
    // using common driver:
    private val driver: Driver = Neo4J.driver
    private var pathLengths: MutableList<Int> = mutableListOf()
    var startNodeId: Int = -1
    var goalNodeIds: List<Int> = listOf()
    fun get(): List<Int> = pathLengths
    private fun getAttackGoal(): String {
        var attackGoal = ""
        try {
                val sc = Scanner(File(filePath))
                var foundGoal = false
                while (!foundGoal && sc.hasNextLine()) {
                    val line = sc.nextLine().filter { !it.isWhitespace() }
                    if ("attackGoal" in line) {
                        attackGoal = line.substringAfter("attackGoal(").substringBefore(if ("_" in line) "_" else ")")
                        foundGoal = true
                    }
                }
                println("Attack goal is: " + attackGoal)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return attackGoal
    }

    fun traverse(x: Node, visited: Set<Int>, length: Int) {
        println(x.getId())
        println(x.getPermission())
        if(x.getPermission().length > 9) {
            println(x.getPermission().substring(1,9))
        }
        if (x.getPermission().length > 9 && x.getPermission().substring(1, 9) == "execCode") {
            println("woo")
            pathLengths.add(length)
        }
        for (r in x.getConnections()) {
            if (!(r.getDest().getId() in visited)) {
                traverse(r.getDest(), visited + x.getId(), length + 1)
            }
        }
    }

    fun update() {
        /*
        val session: Session = driver.session()
        startNodeId = session.writeTransaction { tx -> 
            val result: Result =
                tx.run("MATCH (n:Fact) WHERE n.text STARTS WITH 'attackerLocated' RETURN n.node_id")
            result.list().map { e -> e[0].toString().toInt() }.get(0)
        }
        val attackGoal: String = getAttackGoal()
        goalNodeIds = session.writeTransaction { tx -> 
            val result: Result =
                tx.run("MATCH (n:Permission) WHERE n.text STARTS WITH '${attackGoal}' RETURN n.node_id")
            result.list().map { e -> e[0].toString().toInt() }
        }
        for (goalNodeId in goalNodeIds) {
            val pathLengthsForCurrentGoal: List<Int> = session.writeTransaction { tx ->
                val result: Result = tx.run("MATCH(a: Fact {node_id: ${startNodeId}}), (b: Permission {node_id: ${goalNodeId}}), p=(a)-[*]->(b) RETURN p")
                result.list {r -> r.get(0).size()}
            }
            pathLengths += pathLengthsForCurrentGoal.toTypedArray()
        }*/
        traverse(attackGraph.nodes[0]!!, setOf(), 0)
        pathLengths.sort()
        println(pathLengths)
    }
}
