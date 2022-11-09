package com.neo4j

import org.neo4j.driver.*
import org.neo4j.driver.Values.parameters

class Neo4JAdapter {

    private val driver: Driver = GraphDatabase.driver(
        "neo4j+s://42ce3f9a.databases.neo4j.io",
        AuthTokens.basic("neo4j", "qufvn4LK6AiPaRBIWDLPRzFh4wqzgI5x_n2bXHc1d38")
    )

    private final val attackGraph: Node = buildAttackGraph()

    fun getGraph(): Node {
        return attackGraph
    }

    private fun buildAttackGraph(): Node {
        val ruleNodes : MutableList<Int> = mutableListOf()
        for (id in attackerLocatedNodes()) {
            ruleNodes.add(connectedRule(id))
        }
        val connections : Map<Rule, Node> = buildRules(ruleNodes)
        return Node("none", connections)
    }

    private fun buildNode(id: Int): Node {
        val permission: String = getNodeText(id)
        val connections : Map<Rule, Node> = buildRules(connectedRules(id))
        return Node(permission, connections)
    }

    private fun buildRules(ids: List<Int>) : Map<Rule,Node> {
        val rules : MutableMap<Rule,Node> = mutableMapOf()
        for (id in ids) {
            val key : Rule = buildRule(id)
            val value : Node = buildNode(connectedPermission(id))
            rules[key] = value
        }
        return rules
    }

    private fun buildRule(id: Int) : Rule {
        val rule : String = getNodeText(id)
        val requirements : List<String> = buildRequirements(connectedFacts(id))
        return Rule(rule, requirements)
    }

    private fun buildRequirements(ids: List<Int>) : List<String> {
        val result : MutableList<String> = mutableListOf()
        for (id in ids) {
            result.add(getNodeText(id))
        }
        return result
    }

    private fun connectedPermission(id: Int) : Int {
        val session: Session = driver.session()
        return session.writeTransaction { tx ->
            val result: Result = tx.run(
                "MATCH(start {node_id: $id})-[:To]->(end:Permission) RETURN end.node_id", parameters()
            )
            result.list()[0].get(0).toString().toInt()
        }
    }

    private fun connectedRules(id: Int) : List<Int> {
        val session: Session = driver.session()
        return session.writeTransaction { tx ->
            val result: Result = tx.run(
                "MATCH(start {node_id: $id})-[:To]->(end:Rule) RETURN end.node_id", parameters()
            )
            result.list {r -> r.get(0).toString().toInt()}
        }
    }

    private fun connectedRule(id: Int) : Int {
        val session: Session = driver.session()
        return session.writeTransaction { tx ->
            val result: Result = tx.run(
                "MATCH(start {node_id: $id})-[:To]->(end:Rule) RETURN end.node_id", parameters()
            )
            result.list()[0].get(0).toString().toInt()
        }
    }

    private fun connectedFacts(id: Int) : List<Int> {
        val session: Session = driver.session()
        return session.writeTransaction { tx ->
            val result: Result = tx.run(
                "MATCH(start {node_id: $id})<-[:To]-(end: Fact) RETURN end.node_id", parameters()
            )
            result.list {r -> r.get(0).toString().toInt()}
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

    private fun attackerLocatedNodes() : List<Int> {
        val session: Session = driver.session()
        return session.writeTransaction { tx ->
            val result: Result = tx.run(
                "MATCH(x) WHERE x.text STARTS WITH \"attackerLocated\" RETURN x.node_id",
                parameters()
            )
            result.list {r -> r.get(0).toString().toInt()}
        }
    }

    /* Can there ever be 2 or more attackLocated nodes?
    *  Can an attackerLocated node ever lead to 2 or more rules/permissions.
    *  !DEPRECIATED */
    fun getStartNode(): Int {
        val session: Session = driver.session()
        return session.writeTransaction { tx ->
            val result: Result = tx.run(
                "MATCH(x)-[:To]->(z)-[:To]->(y) WHERE x.text STARTS WITH \"attackerLocated\" RETURN y.node_id",
                parameters()
            )
            result.list()[0].get(0).toString().toInt()
        }
    }

    /* !DEPRECIATED */
    fun getConnectedNodes(id: Int): List<Int> {
        val session: Session = driver.session()
        val connectedNodeIds: List<Int> = session.writeTransaction { tx ->
            val result: Result =
                tx.run("MATCH(start {node_id: $id})-[:To]->(end) RETURN (end.node_id)", parameters("id", id))
            result.list().map { e -> e[0].toString().toInt() }
        }
        return connectedNodeIds
    }
}

fun printNode(n : Node) {
    println(n.permission)
    for (node in n.connections.values) {
        printNode(node)
    }
}

fun main(args : Array<String>) {
    val adapter : Neo4JAdapter = Neo4JAdapter()
    printNode(adapter.getGraph())
}

class Node(
    final val permission: String,
    final val connections: Map<Rule, Node>
) {}

class Rule(
    final val rule: String,
    final val requirements: List<String>
) {}