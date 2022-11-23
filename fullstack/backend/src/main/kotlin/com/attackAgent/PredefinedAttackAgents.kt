package com.attackAgent

import com.neo4j.Node
import com.neo4j.Rule

abstract class PredefinedAttackAgent : AttackAgent() {
    // cloning the original map
    private val TECHNIQUE_LIKELIHOOD_MAP = TECHNIQUE_EASYNESS_MAP.toMutableMap()

    protected lateinit var priorityTechniques: List<String>

    private fun changeOrAddScore(technique: String, score: Int) {
        TECHNIQUE_LIKELIHOOD_MAP.put(technique, score)
    }

    fun updateScores() {
        for (technique in priorityTechniques) {
            changeOrAddScore(technique, HIGH_SCORE)
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
        priorityTechniques = listOf<String>("Exploit Public-Facing Application",
                                            "Active Scanning",
                                            "Encrypted Channel",
                                            "Data Encrypted for Impact")

        updateScores()
    }
}