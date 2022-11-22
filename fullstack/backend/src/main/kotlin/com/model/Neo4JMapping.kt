package com.model

import com.controller.Neo4JController
import java.util.*
import kotlin.collections.HashMap

// for global access to active Neo4jControllers
object Neo4JMapping {
  private var list = HashMap<String, Neo4JController>()

  fun add(neo4JController: Neo4JController): Boolean {
    list[neo4JController.getName()] = neo4JController
    return true
  }

  fun get (name: String): Optional<Neo4JController> {
    return if (list.containsKey(name)) {
      Optional.ofNullable(list[name])
    } else {
      Optional.empty()
    }
  }
}