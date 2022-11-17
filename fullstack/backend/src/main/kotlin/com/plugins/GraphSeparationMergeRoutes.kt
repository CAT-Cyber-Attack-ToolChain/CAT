package com.plugins

import com.beust.klaxon.JsonReader

import io.ktor.server.routing.*
import java.io.StringReader

fun Route.GraphSeparationMergeRoutes() {

    route("/graph/separate") {
        post {

        }
    }
}


private fun <T> toKotlinList(jsonList: String, extract: (reader: JsonReader) -> T): List<T> {
    val list = mutableListOf<T>()
    JsonReader(StringReader(jsonList)).use {
        reader -> reader.beginArray {
            while (reader.hasNext()) {
                list.add(extract(reader))
            }
        }
    }
    return list
}

private fun nodeExtract(reader: JsonReader): Int {
    val nId = reader.nextString()
    return nId.drop(1).toInt()
}

private fun edgeExtract(reader: JsonReader): Pair<Int, Int> {
    var sourceStr = ""
    var targetStr = ""
    reader.beginObject {
        while(reader.hasNext()) {
            val next = reader.nextName()
            when (next) {
                "source" -> sourceStr = reader.nextString()
                "target" -> targetStr = reader.nextString()
            }
        }
    }
    return Pair(sourceStr.drop(1).toInt(), targetStr.drop(1).toInt())
}