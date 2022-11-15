package com.attackAgent

import com.neo4j.Rule
import org.apache.commons.csv.CSVFormat

import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths


val pathToCSV: String = "${System.getProperty("user.dir")}/mulval_mitre_map.csv"

fun readCsv(inputStream: InputStream): List<MulvalMitreRecord> =
    CSVFormat.Builder.create(CSVFormat.DEFAULT).apply {
        setIgnoreSurroundingSpaces(true)
    }.build().parse(inputStream.reader())
        .drop(1) // Dropping the header
        .map {
            MulvalMitreRecord(it[0], it[1])
        }

val mulval_mitre_map = readCsv(Files.newInputStream(Paths.get(pathToCSV)))

fun getMitreTechnique(rule: Rule): MitreTechnique {
    val index = searchMap(rule.rule)
    if (index != -1)
        return MitreTechnique(getTechniqueString(index))
    return MitreTechnique.nullTechnique
}



private fun getTechniqueString(index: Int): String {
    var technique = mulval_mitre_map.get(index).technique

    if (technique.isEmpty()) {
        var i = index
        while (technique.isEmpty()) {
            i--
            technique = mulval_mitre_map.get(i).technique
        }
    }
    return technique

}

private fun searchMap(rule: String): Int {

    val ruleDesc = rule.substring(rule.indexOf('(') + 1, rule.indexOf(')'))
    for (i in mulval_mitre_map.indices) {
        if (mulval_mitre_map.get(i).mulvalRule.contains(ruleDesc.trim()))
            return i
    }
    return -1
}

data class MitreTechnique(val technique: String) {
    companion object {
        val nullTechnique = MitreTechnique("")
    }
}

data class MulvalMitreRecord(val technique: String, val mulvalRule: String)

val TEHCNIQUE_EASYNESS_MAP = mapOf(
    "Drive-by Compromise" to 12,
    "Exploit Public-Facing Application" to 1,
    "External Remote Services" to 0,
    "Hardware Additions" to 0,
    "Phishing" to 0,
    "Replication Through Removable Media" to 0,
    "Supply Chain Compromise" to 0,
    "Trusted Relationship" to 0,
    "Valid Accounts" to 0,

    "Command and Scripting Interpreter" to 0,
    "Container Administration Command" to 0,
    "Exploitation for Client Execution" to 0,
    "Inter-Process Communication" to 0,
    "Native API" to 0,
    "Scheduled Task/Job" to 0,
    "Serverless Execution" to 0,
    "Shared Modules" to 0,
    "Software Deployment Tools" to 0,
    "System Services" to 0,
    "User Execution" to 0,
    "Windows Management Instrumentation" to 0,

    "Exploitation of Remote Services" to 0,
    "Remote Services" to 0,

    "Browser Session Hijacking" to 0,
    "Data from Network Shared Drive" to 0,
    "Data from Local System" to 0


    )