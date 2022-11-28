package com.attackAgent

import com.neo4j.Neo4JAdapter
import com.neo4j.Node
import com.neo4j.Rule

abstract class AttackAgent {

  private val path: MutableList<RuleNodePair> = mutableListOf()
  private val adapter: Neo4JAdapter = Neo4JAdapter()

  fun attack() {
    val startNode = adapter.getGraph()
    var currNode = startNode

    while (currNode.getConnections().isNotEmpty()) {
      var rule = chooseRule(currNode)
      path.add(RuleNodePair(currNode, rule))
      currNode = rule.getDest()
    }
  }

  protected abstract fun chooseRule(n: Node): Rule

  fun printPath() {
    for (pair in path) {
      println(pair.rule.getText())
      println(pair.node.getPermission())
    }
  }


  //Converts path to a pairs of string
  fun returnPath(): MutableList<Pair<String, String>> {
    val paths = mutableListOf<Pair<String, String>>()

    for (ruleNodePair: RuleNodePair in path) {

      paths.add(Pair("n${ruleNodePair.node.getId()}", "n${ruleNodePair.rule.getDest().getId()}"))
    }

    return paths
  }

}

class RuleNodePair(
        val node: Node,
        val rule: Rule
) {}