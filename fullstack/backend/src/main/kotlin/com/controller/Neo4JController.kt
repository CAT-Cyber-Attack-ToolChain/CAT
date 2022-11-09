package com.controller

import com.model.AttackGraphOutput
import com.opencsv.CSVReader
import com.example.model.PathCache
import org.neo4j.driver.AuthTokens
import org.neo4j.driver.Driver
import org.neo4j.driver.GraphDatabase
import org.neo4j.driver.Session
import org.neo4j.driver.Values.parameters
import java.io.FileNotFoundException
import java.io.FileReader
import java.nio.charset.StandardCharsets

class Neo4JController(private val dir: AttackGraphOutput, private val cache: PathCache) {

    private var vertices = mutableListOf<List<String>>()
    private var arcs = mutableListOf<List<String>>()
    private var hasData = false

    companion object {
        val driver: Driver = GraphDatabase.driver(
                "neo4j+s://42ce3f9a.databases.neo4j.io",
                AuthTokens.basic("neo4j", "qufvn4LK6AiPaRBIWDLPRzFh4wqzgI5x_n2bXHc1d38")
        )

        fun close() {
            driver.close()
        }

        /*
        E.g. file.csv

             Hello,My            [["Hello","My"],
             Name,Is        ===>  ["Name","Is"],
             Bob                  ["Bob"]]
         */
        fun readCSV(filepath: String, storage: MutableList<List<String>>) {
            val fr = FileReader(filepath, StandardCharsets.UTF_8)

            fr.use {
                val reader = CSVReader(fr)

                reader.use { r ->
                    var line = r.readNext()

                    while (line != null) {
                        val items = mutableListOf<String>()
                        line.forEach {
                            items.add(it)
                        }

                        storage.add(items)
                        line = r.readNext()
                    }
                }
            }
        }
    }

    private fun readData() {
        hasData = try {
            readVerticesCSV()
            readRelationsCSV()
            true
        } catch (e: FileNotFoundException){
            print("attack graph output not found...")
            false
        }
    }


    private fun readVerticesCSV() {
        readCSV("${dir.getPath()}/VERTICES.csv", vertices)
    }

    private fun readRelationsCSV() {
        readCSV("${dir.getPath()}/ARCS.csv", arcs)
    }

    // TODO:check for successful database query
    private fun generateGraph() {
        flushGraph()
        generateVertices()
        generateRelations()
    }

    private fun generateVertices() {
        val session: Session = driver.session()
        val query = StringBuilder()
        for (vertex: List<String> in vertices) {

            val nodeType = when (vertex[2]) {
                "AND" -> "Rule"
                "OR" -> "Permission"
                else -> "Fact"
            }
            query.append("""CREATE (:${nodeType} {node_id: toInteger(${vertex[0]}), text: "${vertex[1]}", type: "${vertex[2]}", bool:toInteger(${vertex[3]})}) """)
        }
        session.writeTransaction { tx ->
            tx.run(query.toString(), parameters())
        }

    }

    private fun generateRelations() {
        val session: Session = driver.session()
        for (arc: List<String> in arcs) {
            val query = """MATCH (dst {node_id: toInteger(${arc[0]})}) 
                           MATCH (src {node_id: toInteger(${arc[1]})}) 
                           CREATE (src) -[r:To {step: toInteger(${arc[2]})}]-> (dst)"""
            session.writeTransaction { tx ->
                tx.run(query, parameters())
            }
        }
    }

    private fun flushGraph() {
        val session: Session = driver.session()
        session.writeTransaction { tx ->
            tx.run("MATCH (n) DETACH DELETE n ", parameters())
        }
    }

    fun getGraph() {
        val session: Session = driver.session()
        val result: List<String> = session.writeTransaction { tx ->
            val result: org.neo4j.driver.Result = tx.run("MATCH(n) RETURN n", parameters())
            result.list { r -> r.toString() }
        }
        println(result)
    }

    fun update() : Boolean {
        print("Sending attack graph to Neo4j AuraDB...")
        hasData = false
        readData()
        return if (hasData) {
            generateGraph()
            cache.update()
            println("Done!")
            true
        } else {
            println("Failed!")
            false
        }
    }
}