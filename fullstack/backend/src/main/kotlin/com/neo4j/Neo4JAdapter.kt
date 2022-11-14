package com.neo4j

import com.attackAgent.getMitreTechnique
import org.neo4j.driver.*
import org.neo4j.driver.Values.parameters

class Neo4JAdapter {

    private val driver: Driver = GraphDatabase.driver(
        "neo4j+s://42ce3f9a.databases.neo4j.io",
        AuthTokens.basic("neo4j", "qufvn4LK6AiPaRBIWDLPRzFh4wqzgI5x_n2bXHc1d38")
    )

    val nodes: MutableMap<Int, Node> = mutableMapOf()
    private val attackGraph: Node = buildAttackGraph()

    fun getGraph(): Node {
        return attackGraph
    }

    private fun buildAttackGraph(): Node {
        val ruleNodes: MutableList<Int> = mutableListOf()
        for (rule: Int in connectedRule(attackerLocatedNode())) {
            ruleNodes.add(rule)
        }
        val connections: Map<Rule, Int> = buildRules(ruleNodes)
        val node = Node(0, "start", connections)
        nodes[0] = node
        return node
    }

    /* id required to be id of a permission node */
    private fun buildNode(id: Int): Int {
        if (!nodes.containsKey(id)) {
            val permission: String = getNodeText(id)
            val connections: Map<Rule, Int> = buildRules(connectedRules(id))
            nodes[id] = Node(id, permission, connections)
        }
        return id
    }

    /* ids required to be ids of rule nodes */
    private fun buildRules(ids: List<Int>): Map<Rule, Int> {
        val rules: MutableMap<Rule, Int> = mutableMapOf()
        for (id in ids) {
            val key: Rule = buildRule(id)
            val value: Int = buildNode(connectedPermission(id))
            rules[key] = value
        }
        return rules
    }

    /* id required to be id of a rule node */
    private fun buildRule(id: Int): Rule {
        val rule: String = getNodeText(id)
        val requirements: List<String> = buildRequirements(connectedFacts(id))
        return Rule(id, rule, requirements)
    }

    /* ids required to be ids of fact nodes */
    private fun buildRequirements(ids: List<Int>): List<String> {
        val result: MutableList<String> = mutableListOf()
        for (id in ids) {
            result.add(getNodeText(id))
        }
        return result
    }

    /* id required to be id of a rule node */
    private fun connectedPermission(id: Int): Int {
        val session: Session = driver.session()
        return session.writeTransaction { tx ->
            val result: Result = tx.run(
                "MATCH(start {node_id: $id})-[:To]->(end:Permission) RETURN end.node_id", parameters()
            )
            result.list()[0].get(0).toString().toInt()
        }
    }

    /* id required to be id of a permission node */
    private fun connectedRules(id: Int): List<Int> {
        val session: Session = driver.session()
        return session.writeTransaction { tx ->
            val result: Result = tx.run(
                "MATCH(start {node_id: $id})-[:To]->(end:Rule) RETURN end.node_id", parameters()
            )
            result.list { r -> r.get(0).toString().toInt() }
        }
    }

    /* id required to be id of a fact node */
    private fun connectedRule(id: Int): List<Int> {
        val session: Session = driver.session()
        return session.writeTransaction { tx ->
            val result: Result = tx.run(
                "MATCH(start {node_id: $id})-[:To]->(end:Rule) RETURN end.node_id", parameters()
            )
            result.list { r -> r.get(0).toString().toInt() }
        }
    }

    /* id required to be id of a rule node */
    private fun connectedFacts(id: Int): List<Int> {
        val session: Session = driver.session()
        return session.writeTransaction { tx ->
            val result: Result = tx.run(
                "MATCH(start {node_id: $id})<-[:To]-(end: Fact) RETURN end.node_id", parameters()
            )
            result.list { r -> r.get(0).toString().toInt() }
        }
    }

    private fun getNodeText(id: Int): String {
        val session: Session = driver.session()
        return session.writeTransaction { tx ->
            val result: Result = tx.run(
                "MATCH(n {node_id: $id}) RETURN n.text", parameters()
            )
            result.list()[0].get(0).toString()
        }
    }

    private fun attackerLocatedNode(): Int {
        val session: Session = driver.session()
        return session.writeTransaction { tx ->
            val result: Result = tx.run(
                "MATCH(x) WHERE x.text STARTS WITH \"attackerLocated\" RETURN x.node_id",
                parameters()
            )
            result.list()[0].get(0).toString().toInt()
        }
    }
}

val adapter: Neo4JAdapter = Neo4JAdapter()

fun printNode(n: Node) {
    println(n.permission)
    for (node in n.connections.values) {
        printNode(adapter.nodes[node]!!)
    }
}

fun main(args: Array<String>) {

    for (n: Node in adapter.nodes.values) {
        println(n.permission)
    }
//    printNode(adapter.getGraph())
}

class Node(
    val id: Int,
    val permission: String,
    val connections: Map<Rule, Int>
) {}

class Rule(
    val id: Int,
    val rule: String,
    val facts: List<String>
) {

    companion object {
        val DEFAULT_EASYNESS = Int.MAX_VALUE
    }

    var easyness: Int = DEFAULT_EASYNESS

    fun calculateEasynessScore() {
        val technique = getMitreTechnique(this)
        //TODO: come up with heuristic to calculate easyness from technique
        println(technique.technique)
    }
}