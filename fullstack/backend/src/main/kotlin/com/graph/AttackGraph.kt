package com.graph

import com.attackAgent.MitreTechnique
import com.attackAgent.TECHNIQUE_EASYNESS_MAP
import com.attackAgent.getMitreTechnique
import com.beust.klaxon.Klaxon
import com.controller.Controller
import com.controller.Neo4J
import com.cytoscape.CytoDataWrapper
import com.cytoscape.CytoEdge
import com.cytoscape.CytoNode
import com.model.MachineExtractor
import com.view.Updatable
import org.neo4j.driver.Result
import org.neo4j.driver.Session
import org.neo4j.driver.Values.parameters
import java.util.*

class AttackGraph : Updatable, Controller() {

  var nodes: MutableMap<Int, Node> = mutableMapOf()
  private var attackGraph: Node? = null

  fun getGraph(): Node {
    return attackGraph?:Node(0, "start", mutableSetOf())
  }

  fun exportToCytoscapeJSON(): String {
    println("export to json")
    val klaxon = Klaxon()
    val nodestrlist: List<String> = this.nodes.values.map { n ->

      val dataWrappers = nodeToCytoJSON(n)
      val nodeStrs = dataWrappers.map { dw -> klaxon.toJsonString(dw) }
      nodeStrs.joinToString()
    }

    return "[" + nodestrlist.joinToString() + "]"
  }

  private fun nodeToCytoJSON(n: Node): List<CytoDataWrapper> {
    val result: LinkedList<CytoDataWrapper> = LinkedList()
    val node = CytoNode("n${n.getId()}", n.getPermission())
    node.addProperty("machines", MachineExtractor.extract(n.getPermission()))
    result.add(CytoDataWrapper(node))
    n.getConnections().forEach { rule -> result.add(CytoDataWrapper(ruleToCytoEdge(rule, n))) }
    return result
  }

  private fun ruleToCytoEdge(rule: Rule, n: Node): CytoEdge {
    val technique = getMitreTechnique(rule)
    var label = technique.technique
    if (technique == MitreTechnique.nullTechnique)
      label = rule.getText()
    return CytoEdge("e${rule.getId()}", "n${n.getId()}", "n${rule.getDest().getId()}", label)
  }

  override fun update() {
    attackGraph = optimisedBuild()
    //attackGraph = buildAttackGraph()
  }

  /*private fun buildAttackGraph(): Node {
    println("building attack graph")
    val ruleNodeIds: MutableList<Int> = mutableListOf()
    for (rule: Int in connectedRule(attackerLocatedNode())) {
      ruleNodeIds.add(rule)
    }
    val connections: MutableSet<Rule> = buildRules(ruleNodeIds)
    val node = Node(0, "start", connections)
    nodes[0] = node
    //this.notifyObservers()
    return node
  }*/

  private fun optimisedBuild(): Node {
    nodes = mutableMapOf()
    attackGraph = null

    val session: Session = Neo4J.driver!!.session()
    println("building attack graph")

    val permissions = session.writeTransaction { tx ->
      val result: Result = tx.run(
              "MATCH(n:Permission) RETURN n.node_id as id, n.text as text", parameters()
      )
      result.list { r -> Node(r["id"].toString().toInt(), r["text"].toString(), mutableSetOf()) }
    }
    for (node in permissions) {
      nodes[node.getId()] = node
    }
    println(permissions)

    session.writeTransaction { tx ->
      val result: Result = tx.run(
              "MATCH(x:Permission)-[:To]->(z:Rule)-[:To]->(y:Permission) RETURN z.node_id AS ruleId, z.text AS text, x.node_id AS start, y.node_id AS dest", parameters()
      )
      for (r in result.list()) {
        nodes[r["start"].toString().toInt()]!!.addRule(Rule(r["ruleId"].toString().toInt(), r["text"].toString(), nodes[r["dest"].toString().toInt()]!!))
      }
    }
    println(nodes)

    nodes[0] = Node(0, "start", mutableSetOf())
    session.writeTransaction { tx ->
      val result: Result = tx.run(
              "MATCH(f:Fact)-[:To]->(r:Rule)-[:To]->(p:Permission) WHERE (f.text STARTS WITH \"attackerLocated\") RETURN r.node_id AS ruleId, r.text AS text, p.node_id AS dest", parameters()
      )
      for (r in result.list()) {
        nodes[0]!!.addRule(Rule(r["ruleId"].toString().toInt(), r["text"].toString(), nodes[r["dest"].toString().toInt()]!!))
      }
    }
    println("built attack graph")

    this.notifyObservers()

    return nodes[0]!!
  }


  /* id required to be id of a permission node */
  /*private fun buildNode(id: Int): Node {
    if (!nodes.containsKey(id)) {
      val permission: String = getNodeText(id)
      nodes[id] = Node(id, permission, setOf())
      val connections: Set<Rule> = buildRules(connectedRules(id))
      nodes[id] = Node(id, permission, connections)
    }
    return nodes[id]!!
  }

  /* ids required to be ids of rule nodes */
  private fun buildRules(ids: List<Int>): MutableSet<Rule> {
    val rules: MutableSet<Rule> = mutableSetOf()
    for (id in ids) {
      val rule: Rule = buildRule(id)
      rules.add(rule)
    }
    return rules
  }

  /* id required to be id of a rule node */
  private fun buildRule(id: Int): Rule {
    val rule: String = getNodeText(id)
    val dest: Node = buildNode(connectedPermission(id))
    return Rule(id, rule, dest)
  }

  /* id required to be id of a rule node */
  private fun connectedPermission(id: Int): Int {
    val session: Session = Neo4J.driver!!.session()
    return session.writeTransaction { tx ->
      val result: Result = tx.run(
        "MATCH(start {node_id: $id})-[:To]->(end:Permission) RETURN end.node_id", parameters()
      )
      result.list()[0].get(0).toString().toInt()
    }
  }

  /* id required to be id of a permission node */
  private fun connectedRules(id: Int): List<Int> {
    val session: Session = Neo4J.driver!!.session()
    return session.writeTransaction { tx ->
      val result: Result = tx.run(
        "MATCH(start {node_id: $id})-[:To]->(end:Rule) RETURN end.node_id", parameters()
      )
      result.list { r -> r.get(0).toString().toInt() }
    }
  }

  /* id required to be id of a fact node */
  private fun connectedRule(id: Int): List<Int> {
    val session: Session = Neo4J.driver!!.session()
    return session.writeTransaction { tx ->
      val result: Result = tx.run(
        "MATCH(start {node_id: $id})-[:To]->(end:Rule) RETURN end.node_id", parameters()
      )
      result.list { r -> r.get(0).toString().toInt() }
    }
  }

  private fun getNodeText(id: Int): String {
    val session: Session = Neo4J.driver!!.session()
    return session.writeTransaction { tx ->
      val result: Result = tx.run(
        "MATCH(n {node_id: $id}) RETURN n.text", parameters()
      )
      result.list()[0].get(0).toString().replace("\"", "")
    }
  }

  private fun attackerLocatedNode(): Int {
    val session: Session = Neo4J.driver!!.session()
    return session.writeTransaction { tx ->
      val result: Result = tx.run(
        "MATCH(x) WHERE x.text STARTS WITH \"attackerLocated\" RETURN x.node_id",
        parameters()
      )
      result.list()[0].get(0).toString().toInt()
    }
  }

   */
}


fun main() {
  val adapter = AttackGraph()
  for (n: Node in adapter.nodes.values) {
    println(String.format("Node: ${n.getPermission()}"))
    for (r: Rule in n.getConnections()) {
      println(String.format("    - ${r.getText()}"))
    }
  }
}

class Node(
        private val id: Int,
        private val permission: String,
        private val connections: MutableSet<Rule>
) {
  fun getId(): Int {
    return id
  }

  fun getPermission(): String {
    return permission
  }

  fun getConnections(): Set<Rule> {
    return connections
  }

  fun addRule(r: Rule) {
    connections.add(r)
  }
}

class Rule(
        private val id: Int,
        private val text: String,
        private val dest: Node
) {

  companion object {
    const val DEFAULT_EASINESS = Int.MAX_VALUE
  }

  var easiness: Int = DEFAULT_EASINESS

  fun getId(): Int {
    return id
  }

  fun getText(): String {
    return text
  }

  fun getDest(): Node {
    return dest
  }

  fun calculateEasinessScore() {
    easiness = calculateScore(TECHNIQUE_EASYNESS_MAP)
  }

  fun calculateScore(scoreMap: Map<String, Int>): Int {
    val technique = getMitreTechnique(this)
    return scoreMap.getOrDefault(technique.technique, 0)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is Rule) return false
    if (text == other.text) return true
    return false
  }

  override fun hashCode(): Int {
    var result = text.hashCode()
    result = 31 * result + dest.hashCode()
    return result
  }

}