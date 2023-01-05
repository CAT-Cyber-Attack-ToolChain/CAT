package com.model

import org.neo4j.driver.*

import java.io.File
import java.io.IOException
import java.util.*
import com.controller.Neo4J


class PathCache(val filePath: String) {
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
    fun update() {
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
        }
        pathLengths = pathLengths.sorted() as MutableList<Int>
    }
}
