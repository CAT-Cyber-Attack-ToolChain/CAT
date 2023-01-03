package com.attackAgent

import com.graph.Rule
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
    val index = searchMap(rule.getText())
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


// no. of procedures listed on the Mitre Att&ck website for each technique
val TECHNIQUE_PROCEDURE_NUMBER_MAP = mapOf(
    "Drive-by Compromise" to 36,
    "Exploit Public-Facing Application" to 31,
    "External Remote Services" to 27,
    "Hardware Additions" to 1,
    "Phishing" to 191,
    "Replication Through Removable Media" to 22,
    "Supply Chain Compromise" to 11,
    "Trusted Relationship" to 10,
    "Valid Accounts" to 77,

    "Command and Scripting Interpreter" to 300,
    "Container Administration Command" to 5,
    "Exploitation for Client Execution" to 50,
    "Inter-Process Communication" to 35,
    "Native API" to 151,
    "Scheduled Task/Job" to 154,
    "Serverless Execution" to 0,
    "Shared Modules" to 16,
    "Software Deployment Tools" to 4,
    "System Services" to 61,
    "User Execution" to 219,
    "Windows Management Instrumentation" to 96,

    "Exploitation of Remote Services" to 23,
    "Remote Services" to 122,

    "Browser Session Hijacking" to 11,
    "Data from Network Shared Drive" to 12,
    "Data from Local System" to 168
    )

val TECHNIQUE_EASYNESS_MAP = TECHNIQUE_PROCEDURE_NUMBER_MAP