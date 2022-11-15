package com.example.model

import org.neo4j.driver.*

import java.io.File
import java.io.IOException
import java.util.*


class PathCache(val filePath: String) {
    private val driver: Driver = GraphDatabase.driver("neo4j+s://42ce3f9a.databases.neo4j.io", AuthTokens.basic("neo4j", "qufvn4LK6AiPaRBIWDLPRzFh4wqzgI5x_n2bXHc1d38"))
    private var pathLengths: List<Int> = mutableListOf()
    var startNodeId: Int = -1;
    var goalNodeIds: List<Int> = listOf<Int>();
    fun get(): List<Int> = pathLengths
    private fun getAttackGoal(): String {
        var attackGoal: String = "";
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
        return attackGoal;
    }
    fun update() {
        val session: Session = driver.session()
        startNodeId = session.writeTransaction { tx -> 
            val result: Result =
                tx.run("MATCH (n:Fact) WHERE n.text STARTS WITH 'attackerLocated' RETURN n.node_id");
            result.list().map { e -> e[0].toString().toInt() }.get(0);
        }
        val attackGoal: String = getAttackGoal();
        goalNodeIds = session.writeTransaction { tx -> 
            val result: Result =
                tx.run("MATCH (n:Permission) WHERE n.text STARTS WITH '${attackGoal}' RETURN n.node_id");
            result.list().map { e -> e[0].toString().toInt() };
        }
        for (goalNodeId in goalNodeIds) {
            val pathLengthsForCurrentGoal: List<Int> = session.writeTransaction { tx ->
                val result: Result = tx.run("MATCH(a: Fact {node_id: ${startNodeId}}), (b: Permission {node_id: ${goalNodeId}}), p=(a)-[*]->(b) RETURN p");
                result.list {r -> r.get(0).size()};
            }
            pathLengths += pathLengthsForCurrentGoal.toTypedArray();
        }
        pathLengths = pathLengths.sorted()
    }
}
