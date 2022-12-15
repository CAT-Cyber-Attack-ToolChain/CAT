package com.attackAgent

import com.neo4j.Node
import com.neo4j.Rule

abstract class PredefinedAttackAgent : AttackAgent() {

    var priorityTechniques: List<String> = listOf()

    var usedPriorityTechnique: Boolean = false
    // cloning the original map
    private val TECHNIQUE_LIKELIHOOD_MAP = TECHNIQUE_EASYNESS_MAP.toMutableMap()

    private fun changeOrAddScore(technique: String, score: Int) {
        TECHNIQUE_LIKELIHOOD_MAP.put(technique, score)
    }

    fun updateScores() {
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
        n.getConnections().forEach {rule ->
            val currentScore = rule.calculateScore(TECHNIQUE_LIKELIHOOD_MAP)
            if (pickedRule == null) {
                pickedRule = rule
                maxScore = currentScore
            } else if (currentScore > maxScore)
                pickedRule = rule
                maxScore = currentScore
        }

        if (priorityTechniques.contains(getMitreTechnique(pickedRule!!).technique)) {
            usedPriorityTechnique = true
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

class REvilAttackAgent : PredefinedAttackAgent() {
    init {
        priorityTechniques = listOf<String>("Access Token Manipulation: Token Impersonation/Theft",
        "Access Token Manipulation: Create Process with Token", "Application Layer Protocol: Web Protocols",
        "Command and Scripting Interpreter", "Data Destruction", "Data Encrypted for Impact", "Deobfuscate/Decode Files or Information",
        "Drive-by Compromise", "Encrypted Channel: Asymmetric Cryptography",
        "Exfiltration Over C2 Channel", "File and Directory Discovery", "Impair Defenses: Disable or Modify Tools",
        "Impair Defenses: Safe Mode Boot", "Indicator Removal: File Deletion", "Ingress Tool Transfer",
        "Inhibit System Recovery", "Masquerading: Match Legitimate Name or Location", "Modify Registry",
        "Native API", "Obfuscated Files or Information", "Permission Groups Discovery: Domain Groups",
        "Phishing: Spearphishing Attachment", "Process Injection", "Query Registry", "Service Stop", "System Information Discovery",
        "System Location Discovery: System Language Discovery", "System Service Discovery", "User Execution: Malicious File", "Windows Management Instrumentation",
        "Loss of Productivity and Revenue", "Masquerading", "Remote Services", "Scripting", "Service Stop",
        "Standard Application Layer Protocol", "Theft of Operational Information", "User Execution")

        /**
        T1134
        T1071
        T1059
        T1485
        T1486
        T1140
        T1189
        T1573
        T1041
        T1083
        T1562
        T1070
        T1105
        T1490
        T1036
        T1112
        T1106
        T1027
        T1069
        T1566
        T1055
        T1012
        T1489
        T1082
        T1614
        T1007
        T1204
        T1047
        T0828
        T0849
        T0886
        T0853
        T0881
        T0869
        T0882
        T0863
         */

        updateScores()
    }
}

class SynAckAttackAgent : PredefinedAttackAgent() {
    init {
        priorityTechniques = listOf("Data Encrypted for Impact", "File and Directory Discovery",
            "Indicator Removal: Clear Windows Event Logs", "Modify Registry", "Native API", "Obfuscated Files or Information",
        "Process Discovery", "Process Injection: Process Doppelganging", "Query Registry", "System Information Discovery",
        "System Location Discovery: System Language Discovery", "System Owner/User Discovery",
        "System Service Discovery", "Virtualization/Sandbox Evasion: System Checks")

        // T1083, T1070, T1112, T1106, T1027, T1057, T1055, T1012, T1082, T1614, T1033, T1007, T1497

        updateScores()
    }
}

class T9000AttackAgent : PredefinedAttackAgent() {
    init {
        priorityTechniques = listOf("Archive Collected Data: Archive via Custom Method", "Audio Capture",
        "Automated Collection", "Event Triggered Execution: AppInit DLLs", "Hijack Execution Flow: DLL Side-Loading",
        "Peripheral Device Discovery", "Screen Capture", "Software Discovery: Security Software Discovery",
        "System Information Discovery", "System Network Configuration Discovery", "System Owner/User Discovery",
        "System Time Discovery", "Video Capture")

        // T1560, T1123, T1119, 12546, T1574, T1120, T1113, T1518, T1082, T1016, T1033, T1124, T1125

        updateScores()
    }
}

class WiperAttackAgent : PredefinedAttackAgent() {
    init {
        priorityTechniques = listOf("Software Deployment Tools")
        // T1072
        updateScores()
    }
}

class CustomAttackAgent(val techniqueMap: Map<String, Int> = mapOf()) : PredefinedAttackAgent() {
    init {
        updateScoresWith(techniqueMap)
    }

    companion object {
        var AGENT = CustomAttackAgent()
    }
}