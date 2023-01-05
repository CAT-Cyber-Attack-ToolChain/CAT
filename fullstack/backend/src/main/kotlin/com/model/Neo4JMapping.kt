package com.model

import com.controller.Neo4J
import java.util.*
import kotlin.collections.HashMap

// for global access to active Neo4jControllers
object Neo4JMapping {
  private var list = HashMap<String, Neo4J>()

  fun add(neo4J: Neo4J): Boolean {
    list[neo4J.getName()] = neo4J
    return true
  }

  fun get (name: String): Optional<Neo4J> {
    return if (list.containsKey(name)) {
      Optional.ofNullable(list[name])
    } else {
      Optional.empty()
    }
  }
}