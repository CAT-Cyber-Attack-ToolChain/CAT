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

fun getMitreTechnique(rule: Rule): MitreTechnique =
    MitreTechnique(getTechniqueString(searchMap(rule.rule)))

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
    for (i in mulval_mitre_map.indices) {
        if (mulval_mitre_map.get(i).mulvalRule == rule)
            return i
    }
    return -1
}

data class MitreTechnique(val technique: String)

data class MulvalMitreRecord(val technique: String, val mulvalRule: String)