package com.attackAgent

import com.graph.AttackGraph
import com.graph.Node
import com.graph.Rule

abstract class AttackAgent {

  companion object {
    fun getAttackResponse(attackAgent: AttackAgent): Pair<MutableList<Pair<String, String>>, Boolean> {
      attackAgent.attack()
      var successful = true
      if (attackAgent is PredefinedAttackAgent) {
        successful = attackAgent.usedPriorityTechnique
      }
      return Pair(attackAgent.returnPath(), successful)
    }
  }

  private val path: MutableList<RuleNodePair> = mutableListOf()
  private val adapter: AttackGraph = AttackGraph()

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