package com.attackAgent

import com.neo4j.Node
import com.neo4j.Rule

abstract class PredefinedAttackAgent : AttackAgent() {
    // cloning the original map
    private val TECHNIQUE_LIKELIHOOD_MAP = TECHNIQUE_EASYNESS_MAP.toMutableMap()

    private fun changeOrAddScore(technique: String, score: Int) {
        TECHNIQUE_LIKELIHOOD_MAP.put(technique, score)
    }

    fun updateScores(priorityTechniques: List<String>) {
        for (technique in priorityTechniques) {
            changeOrAddScore(technique, HIGH_SCORE)
        }
    }

    fun updateScoresWith(techniqueMap: Map<String, Int>) {
        for (entry in techniqueMap.entries) {
            changeOrAddScore(entry.key, entry.value)
        }
    }

    override fun chooseRule(n: Node): Rule {
        var pickedRule: Rule? = null
        var maxScore = Int.MIN_VALUE
        n.connections.forEach {rule ->
            val currentScore = rule.calculateScore(TECHNIQUE_LIKELIHOOD_MAP)
            if (pickedRule == null) {
                pickedRule = rule
                maxScore = currentScore
            } else if (currentScore > maxScore)
                pickedRule = rule
                maxScore = currentScore
        }
        return pickedRule!!
    }

    companion object {
        // higher than any easyness
        val HIGH_SCORE = 10000
    }
}

class WannacryAttackAgent : PredefinedAttackAgent() {

    init {
        val priorityTechniques = listOf<String>("Exploit Public-Facing Application",
                                            "Active Scanning",
                                            "Encrypted Channel",
                                            "Data Encrypted for Impact")

        updateScores(priorityTechniques)
    }
}

class CustomAttackAgent(val techniqueMap: Map<String, Int>) : PredefinedAttackAgent() {
    init {
        updateScoresWith(techniqueMap)
    }
}